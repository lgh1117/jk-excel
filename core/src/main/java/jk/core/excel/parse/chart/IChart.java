package jk.core.excel.parse.chart;

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
 * @file: IChart.java
 * @author: liguohui lgh1177@126.com
 * @version: v1.0
 */
public interface IChart {

	/**
	 * 饼图
	 */
	public static final String TYPE_PIE = "pie";

	/**
	 * 柱状图
	 */
	public static final String TYPE_BAR = "bar";

	/**
	 * 折线图
	 */
	public static final String TYPE_LINE = "line";

	/**
	 * <p>
	 * Discription:获取生成报表图片文件
	 * </p>
	 * 
	 * @return
	 */
	File getChartFile() throws IOException;

	/**
	 * <p>
	 * Discription:将生成的报表以流的形式出现
	 * </p>
	 * 
	 * @return
	 * @throws IOException
	 */
	OutputStream getChartStream() throws IOException;

	/**
	 * <p>
	 * Discription:产生报表图的类型，如：饼图--pie等
	 * </p>
	 * 
	 * @return
	 */
	String charType();
}
