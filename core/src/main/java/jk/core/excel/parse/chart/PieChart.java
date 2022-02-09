package jk.core.excel.parse.chart;

import jk.core.ex.ChartException;
import jk.core.util.Utils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: PieChart.java
 * @author: liguohui lgh1177@126.com
 * @version: v1.0
 */
public class PieChart implements IChart {

	private ChartConfig config;

	/**
	 * @param config
	 */
	public PieChart(ChartConfig config) {
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
	 * @throws IOException
	 * @see jk.excel.chart.IChart#getChart()
	 */
	public File getChartFile() throws IOException {
		checkData();
		String pathname = System.getProperty("user.dir");
		File file = new File(pathname + "/chart" + System.currentTimeMillis()
				+ ".jpg");
		JFreeChart chart = generate();
		ChartUtilities.saveChartAsJPEG(file, // 输出到哪个输出流
				1, // JPEG图片的质量，0~1之间
				chart, // 统计图标对象
				640, // 宽
				300,// 宽
				null // ChartRenderingInfo 信息
				);
		return file;
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @throws IOException
	 */
	private JFreeChart generate() throws IOException {
		DefaultPieDataset dataset = getPieDataset();

		JFreeChart chart = ChartFactory.createPieChart(config.getTitle(),
				dataset, true, true, false);
		PiePlot plot = (PiePlot) chart.getPlot();
		// 图片中显示百分比:默认方式
		// plot.setLabelGenerator(new
		// StandardPieSectionLabelGenerator(StandardPieToolTipGenerator.DEFAULT_TOOLTIP_FORMAT));
		// 图片中显示百分比:自定义方式，{0} 表示选项， {1} 表示数值， {2} 表示所占比例 ,小数点后两位
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}({2})",
				NumberFormat.getNumberInstance(), new DecimalFormat("0.00%")));
		// 图例显示百分比:自定义方式， {0} 表示选项， {1} 表示数值， {2} 表示所占比例
		plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}={1}({2})"));
		// 设置背景色为白色
		chart.setBackgroundPaint(Color.white);
		// 指定图片的透明度(0.0-1.0)
		plot.setForegroundAlpha(1.0f);
		// 指定显示的饼图上圆形(false)还椭圆形(true)
		plot.setCircular(true);
		// 设置图标题的字体
		Font font = new Font(config.getTitleFontName(), Font.CENTER_BASELINE,
				config.getTitleFontSize());
		TextTitle title = new TextTitle(config.getTitle());
		title.setFont(font);
		chart.setTitle(title);

		plot.setLabelFont(new Font(config.getLabelFontName(),
				Font.CENTER_BASELINE, config.getLabelFontSize()));//
		LegendTitle legend = chart.getLegend(0);
		legend.setItemFont(new Font(config.getItemFontName(), Font.BOLD, config
				.getItemFontSize()));
		return chart;
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// ChartUtilities.writeChartAsJPEG(out, 1, chart, 640, 300);
		// /(out, // 输出到哪个输出流
		// 1, // JPEG图片的质量，0~1之间
		// chart, // 统计图标对象
		// / 640, // 宽
		// 300,// 高
		// null // ChartRenderingInfo 信息
		// );
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @return
	 */
	private DefaultPieDataset getPieDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (ChartItem item : config.getItems()) {
			if (!Utils.isEmpty(item) && !Utils.isEmpty(item.getName())
					&& !Utils.isEmpty(item.getValue())) {
				dataset.setValue(item.getName(), item.getValue());
			}
		}
		return dataset;
	}

	private void checkData() {
		if (config == null) {
			throw new ChartException(
					"the config data is null for report dashboard!");
		}
		if (config.getItems() == null || config.getItems().size() == 0) {
			throw new ChartException(
					"the item data is null for report dashboard!");
		}
	}

	/**
	 * @see IChart#charType()
	 */
	public String charType() {
		return TYPE_PIE;
	}

	/**
	 * @see IChart#getChartStream()
	 */
	public OutputStream getChartStream() throws IOException {
		JFreeChart chart = generate();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsJPEG(out, 1, chart, 640, 300);
		out.flush();
		out.close();
		return out;
	}

}
