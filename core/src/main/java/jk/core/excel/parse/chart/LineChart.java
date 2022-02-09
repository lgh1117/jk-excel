package jk.core.excel.parse.chart;

import jk.core.ex.ChartException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: 折线图
 * </p>
 * 
 * @file: LineChart.java
 * @author: liguohui lgh1177@126.com
 * @version: v1.0
 */
public class LineChart extends AbstractChart {
	public static final String DATASET_TYPE_XY = "xy";
	public static final String DATASET_TYPE_TIME = "time";
	public static final String DATASET_TYPE_CATEGORY = "category";

	private ChartConfig config;
	private String datasetType;

	public LineChart(ChartConfig config) {
		this.config = config;
		this.datasetType = DATASET_TYPE_CATEGORY;
	}

	public LineChart(ChartConfig config, String datasetType) {
		this.config = config;
		this.datasetType = datasetType;
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
	 * @see jk.excel.chart.AbstractChart#createChart()
	 */
	protected JFreeChart createChart() throws IOException {
		checkData();

		JFreeChart jfreechart = null;
		if (this.datasetType.equals(DATASET_TYPE_TIME)) {
			XYDataset dataset = (XYDataset) createDataset();
			jfreechart = ChartFactory.createTimeSeriesChart(config.getTitle(),
					config.getCategoryName(), config.getyValue(), dataset,
					true, true, false);
			XYPlot xyplot = this.createXYPlot(jfreechart);
			DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
			dateaxis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
		} else if (this.datasetType.equals(DATASET_TYPE_XY)) {
			XYDataset dataset = (XYDataset) createDataset();
			jfreechart = ChartFactory.createXYLineChart(config.getTitle(),
					config.getCategoryName(), config.getyValue(), dataset,
					PlotOrientation.VERTICAL, true, true, false);
			XYPlot xyplot = this.createXYPlot(jfreechart);
			NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
			numberaxis
					.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		} else {
			CategoryDataset dataset = (CategoryDataset) createDataset();
			jfreechart = ChartFactory.createLineChart(config.getTitle(),
					config.getCategoryName(), config.getyValue(), dataset,
					PlotOrientation.VERTICAL, true, true, false);
			setCategoryLineProperties(jfreechart);
		}

		// 设置图标题的字体
		Font font = new Font(config.getTitleFontName(), Font.CENTER_BASELINE,
				config.getTitleFontSize());
		TextTitle title = new TextTitle(config.getTitle());
		title.setFont(font);
		jfreechart.setTitle(title);

		return jfreechart;
	}

	private XYPlot createXYPlot(JFreeChart jfreechart) {
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setDomainPannable(true);
		xyplot.setRangePannable(true);
		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot
				.getRenderer();
		xylineandshaperenderer.setBaseShapesVisible(true);
		xylineandshaperenderer.setBaseShapesFilled(true);
		return xyplot;
	}

	private Dataset createDataset() {
		if (this.datasetType.equals(DATASET_TYPE_TIME)) {
			return this.createTimeDataset();
		} else if (this.datasetType.equals(DATASET_TYPE_XY)) {
			return this.createXYDataset();
		} else {
			return this.createCategoryDataset();
		}
	}

	private XYDataset createXYDataset() {
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();
		for (Object obj : config.getCateItems()) {
			LineItem item = (LineItem) obj;
			if (item != null) {
				XYSeries xyseries = new XYSeries(item.getName());
				List<XYItem> items = item.getItems();
				for (int i = 0; i < items.size(); i++) {
					xyseries.add(
							Double.parseDouble(items.get(i).getX().toString()),
							items.get(i).getY());
				}
				xyseriescollection.addSeries(xyseries);
			}
		}
		return xyseriescollection;
	}

	private XYDataset createTimeDataset() {
		TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
		for (Object obj : config.getCateItems()) {
			LineItem item = (LineItem) obj;
			if (item != null) {
				TimeSeries timeseries = new TimeSeries(item.getName());
				List<XYItem> items = item.getItems();
				for (int i = 0; i < items.size(); i++) {
					timeseries.add(this.getDay(items.get(i).getX()),
							items.get(i).getY());
				}
				timeseriescollection.addSeries(timeseries);
			}
		}
		return timeseriescollection;
	}

	private CategoryDataset createCategoryDataset() {
		DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
		for (Object obj : config.getCateItems()) {
			LineItem lItem = (LineItem) obj;
			if (lItem != null) {
				String categoryName = lItem.getName();
				for (XYItem item : lItem.getItems()) {
					if (item != null) {
						defaultcategorydataset.addValue(item.getY(),
								categoryName, item.getX().toString());
					}
				}
			}
		}

		return defaultcategorydataset;
	}

	private void setCategoryLineProperties(JFreeChart jfreechart) {
		Font itemFont = new Font(config.getLabelFontName(), Font.BOLD, 12);

		CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
		categoryplot.setDomainGridlinesVisible(true);
		categoryplot.setRangeCrosshairVisible(true);
		categoryplot.setRangeCrosshairPaint(Color.blue);

		LineAndShapeRenderer renderer = (LineAndShapeRenderer) categoryplot
				.getRenderer();
		renderer.setItemLabelsVisible(true); // 基本项标签显示
		renderer.setItemLabelPaint(Color.black);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(Boolean.TRUE);
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.OUTSIDE1, TextAnchor.BASELINE_CENTER));
		renderer.setItemLabelAnchorOffset(-1D);
		renderer.setItemLabelFont(itemFont);

		CategoryAxis domainAxis = categoryplot.getDomainAxis();
		domainAxis.setLabelFont(itemFont); // X轴的标题文字字体
		domainAxis.setTickLabelFont(itemFont); // X轴坐标上数值字体
		domainAxis.setTickLabelFont(new Font(config.getLabelFontName(),
				Font.BOLD, 9));
		if (config.getAngle() == 1) {
			domainAxis
					.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		} else {
			if (config.getAngle() < 2.0) {
				throw new ChartException("The angle value less than 2.0");
			}
			BigDecimal an = new BigDecimal(config.getAngle());
			an = (new BigDecimal(Math.PI)).divide(an, 18,
					BigDecimal.ROUND_HALF_UP);
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions
					.createUpRotationLabelPositions(an.doubleValue()));
		}

		ValueAxis valueAxis = categoryplot.getRangeAxis();
		valueAxis.setLabelFont(itemFont);
		valueAxis.setAxisLineVisible(true);
	}

	private Day getDay(Object x) {
		String dateString = x.toString();
		String yearString = dateString.substring(0, dateString.indexOf("-"));
		String monthString = dateString.substring(dateString.indexOf("-") + 1,
				dateString.lastIndexOf("-"));
		String dayString = dateString
				.substring(dateString.lastIndexOf("-") + 1);
		return new Day(Integer.parseInt(dayString),
				Integer.parseInt(monthString), Integer.parseInt(yearString));
	}

	/**
	 * @see IChart#charType()
	 */
	public String charType() {
		return TYPE_LINE;
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
