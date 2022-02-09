package jk.core.excel.parse.chart;

import jk.core.ex.ChartException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: 柱状图
 * </p>
 * 
 * @file: BarChart.java
 * @author: liguohui lgh1177@126.com
 * @version: v1.0
 */
public class BarChart extends AbstractChart {

	private ChartConfig config;

	private List<Color> colors = new ArrayList<Color>();
	{

		colors.add(new Color(74, 107, 168));
		colors.add(new Color(86, 5, 237));
		colors.add(new Color(154, 157, 85));
		colors.add(new Color(236, 173, 6));
		colors.add(new Color(2, 192, 240));
		colors.add(new Color(109, 104, 138));
		colors.add(new Color(176, 66, 69));
		colors.add(new Color(46, 197, 159));
		colors.add(new Color(210, 32, 103));
		colors.add(new Color(227, 116, 15));
		colors.add(Color.cyan);
		colors.add(Color.green);
		colors.add(Color.blue);
		colors.add(Color.yellow);
		colors.add(Color.red);
		colors.add(Color.orange);
		colors.add(Color.darkGray);
		colors.add(Color.pink);
		colors.add(Color.lightGray);
		colors.add(Color.gray);
	}

	/**
	 * @param config
	 */
	public BarChart(ChartConfig config) {
		this.config = config;
	}

	/**
	 * @return the config
	 */
	public ChartConfig getConfig() {
		return config;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(ChartConfig config) {
		this.config = config;
	}

	/**
	 * @see IChart#charType()
	 */
	public String charType() {
		return TYPE_BAR;
	}

	private CategoryDataset createCategoryDataset() {
		DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
		for (Object obj : config.getCateItems()) {
			BarChartItem bItem = (BarChartItem) obj;
			if (bItem != null) {
				String categoryName = bItem.getCategory();
				for (ChartItem item : bItem.getItems()) {
					if (item != null) {
						defaultcategorydataset.addValue(item.getValue(),
								categoryName, item.getName());
					}
				}
			}
		}

		return defaultcategorydataset;
	}

	public JFreeChart createChart() throws IOException {
		checkData();

		CategoryDataset categorydataset = createCategoryDataset();

		JFreeChart jfreechart = ChartFactory.createBarChart3D(
				config.getTitle(), config.getCategoryName(),
				config.getyValue(), categorydataset, PlotOrientation.VERTICAL,
				true, true, false);

		setProperties(jfreechart);

		CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();

		categoryplot.setDomainGridlinesVisible(true);
		categoryplot.setRangeCrosshairVisible(true);
		categoryplot.setRangeCrosshairPaint(Color.blue);

		NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		BarRenderer3D barrenderer = (BarRenderer3D) categoryplot.getRenderer();

		int len = config.getCateItems().size();
		for (int i = 0; i < len; i++) {
			GradientPaint gradientpaint = new GradientPaint(0.0F, 0.0F,
					colors.get(i), 0.0F, 0.0F, new Color(0, 0, 64));
			barrenderer.setSeriesPaint(i, gradientpaint);
			barrenderer.setSeriesItemLabelsVisible(i, true);
		}

		// 设置图标题的字体
		Font font = new Font(config.getTitleFontName(), Font.CENTER_BASELINE,
				config.getTitleFontSize());
		TextTitle title = new TextTitle(config.getTitle());
		title.setFont(font);
		jfreechart.setTitle(title);

		barrenderer
				.setLegendItemToolTipGenerator(new StandardCategorySeriesLabelGenerator(
						"Tooltip: {0}"));
		CategoryAxis categoryaxis = categoryplot.getDomainAxis();

		if (config.getAngle() == 1) {
			categoryaxis
					.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		} else {
			if (config.getAngle() < 2.0) {
				throw new ChartException("The angle value less than 2.0");
			}
			BigDecimal an = new BigDecimal(config.getAngle());
			an = (new BigDecimal(Math.PI)).divide(an, 18,
					BigDecimal.ROUND_HALF_UP);
			categoryaxis.setCategoryLabelPositions(CategoryLabelPositions
					.createUpRotationLabelPositions(an.doubleValue()));
		}

		return jfreechart;
	}

	private void setProperties(JFreeChart chart) {
		Font itemFont = new Font(config.getLabelFontName(), Font.PLAIN, 12);
		// 获取图表区域对象
		CategoryPlot plot = chart.getCategoryPlot();
		// 设置图表的背景颜色
		// plot.setBackgroundPaint(new Color(122, 197, 205));
		// 设置图表纵向网格线颜色
		// plot.setDomainGridlinePaint(Color.red);
		// plot.setDomainGridlineStroke(new BasicStroke());
		// 设置图表横向网格线颜色
		// plot.setRangeGridlinePaint(Color.blue);
		// plot.setRangeGridlineStroke(new BasicStroke());
		// 设置柱子透明度
		// plot.setForegroundAlpha(1.0f);
		// 获取x轴
		CategoryAxis domainAxis = plot.getDomainAxis();
		// 设置x轴标题
		domainAxis.setLabelFont(itemFont);
		// 设置x轴字段
		// x轴竖线颜色
		// domainAxis.setAxisLinePaint(Color.red);
		// domainAxis.setTickLabelFont(itemFont);
		// 同理，y轴
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setLabelFont(itemFont);
		// Y轴竖线颜色
		// rangeAxis.setAxisLinePaint(Color.red);
		// 处理中文乱码问题
		// domainAxis.setUpperMargin(0.3);
		// domainAxis.setLowerMargin(0.3);
		domainAxis.setAxisLineVisible(true);
		// 拿到立体属性对象
		BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
		// 图片背景色
		plot.setOutlineVisible(true);
		// 图边框颜色
		plot.setOutlinePaint(Color.magenta);
		// 设置墙颜色
		// renderer.setWallPaint(Color.LIGHT_GRAY);
		renderer.setMaximumBarWidth(0.1);
		renderer.setMinimumBarLength(0.1);
		renderer.setItemMargin(config.getItemMarge());
		domainAxis.setCategoryMargin(config.getCategoryMarge());// 横轴标签之间的距离20%
		chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		// 让每个柱子上显示对应的value，并设置颜色
		renderer.setItemLabelsVisible(true);
		renderer.setItemLabelPaint(Color.black);
		renderer.setDrawBarOutline(false);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(Boolean.TRUE);
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.OUTSIDE1, TextAnchor.BASELINE_CENTER));
		renderer.setItemLabelAnchorOffset(-1D);

		renderer.setItemLabelFont(itemFont);
	}

	private void checkData() {
		if (config == null) {
			throw new ChartException(
					"the config data is null for report dashboard!");
		}
		if (config.getCateItems() == null || config.getCateItems().size() == 0) {
			throw new ChartException(
					"the item data is null for report dashboard!");
		}
	}
}
