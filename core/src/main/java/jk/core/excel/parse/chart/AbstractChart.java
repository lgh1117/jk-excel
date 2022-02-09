package jk.core.excel.parse.chart;

import org.jfree.chart.*;
import org.jfree.chart.entity.StandardEntityCollection;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: AbstractChart.java
 * @author: liguohui lgh1177@126.com
 * @version: v1.0
 */
public abstract class AbstractChart implements IChart {

	/**
	 * <p>
	 * Discription:保存图标到文件，并返回文件句柄
	 * </p>
	 * 
	 * @param chart
	 * @return
	 * @throws IOException
	 */
	public File saveToFile(JFreeChart chart) throws IOException {
		String pathname = System.getProperty("user.dir");
		File file = new File(pathname + "/chart" + System.currentTimeMillis()
				+ ".jpg");
		ChartRenderingInfo info = new ChartRenderingInfo(
				new StandardEntityCollection());
		ChartUtilities.saveChartAsJPEG(file, // 输出到哪个输出流
				getConfig().getPictureQuality(), // JPEG图片的质量，0~1之间
				chart, // 统计图标对象
				getConfig().getWidth(), // 宽
				getConfig().getHeight(),// 高
				info // ChartRenderingInfo 信息
				);
		return file;
	}

	/**
	 * <p>
	 * Discription:将图标写到输出流
	 * </p>
	 * 
	 * @param chart
	 * @return
	 * @throws IOException
	 */
	public OutputStream createOutputStream(JFreeChart chart) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsJPEG(out, getConfig().getPictureQuality(),
				chart, getConfig().getWidth(), getConfig().getHeight());
		out.flush();
		out.close();
		return out;
	}

	/**
	 * 获取图表文件
	 * 
	 * @throws IOException
	 * @see IChart#getChart()
	 */
	public File getChartFile() throws IOException {
		setCharacterEncoding();
		JFreeChart chart = createChart();
		return saveToFile(chart);
	}

	/**
	 * 获取图表输出流
	 * 
	 * @see IChart#getChartStream()
	 */
	public OutputStream getChartStream() throws IOException {
		setCharacterEncoding();
		JFreeChart chart = createChart();
		return this.createOutputStream(chart);
	}

	/**
	 * <p>
	 * Discription:处理图表中文乱码
	 * </p>
	 */
	public void setCharacterEncoding() {
		ChartConfig config = getConfig();
		Font lblFont = new Font(config.getLabelFontName(),
				Font.CENTER_BASELINE, config.getLabelFontSize());
		StandardChartTheme sct = new StandardChartTheme("CN");
		sct.setExtraLargeFont(lblFont);
		sct.setRegularFont(lblFont);
		sct.setLargeFont(lblFont);
		ChartFactory.setChartTheme(sct);
	}

	/**
	 * <p>
	 * Discription:创建图表
	 * </p>
	 * 
	 * @return
	 * @throws IOException
	 */
	protected abstract JFreeChart createChart() throws IOException;

	/**
	 * <p>
	 * Discription:创建图标所需相关配置信息
	 * </p>
	 * 
	 * @return
	 */
	public abstract ChartConfig getConfig();
}
