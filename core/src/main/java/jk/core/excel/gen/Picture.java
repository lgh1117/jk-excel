package jk.core.excel.gen;

import jk.core.ex.ExportException;
import jk.core.excel.parse.chart.IChart;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: ExcelPicture.java
 * @author: Jack lee
 * @version: v1.0
 */
public class Picture implements Serializable, PictureType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5358836543715409057L;

	/**
	 * 图片文件，相同的图片，流和文件，有一个即可
	 */
	private File file;

	/**
	 * 图片文件流程，相同的图片，流和文件，有一个即可
	 */
	private InputStream inputStream;

	/**
	 * 文件类型
	 */
	private int type;

	/**
	 * 将产生报表的图写入到excel中
	 */
	private IChart chart;

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		this.file = file;
		setType(file);
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param file2
	 */
	private void setType(File f) {
		if (f == null) {
			throw new ExportException("file is null");
		}

		String ext = f.getName();
		ext = ext.substring(ext.lastIndexOf(".") + 1);
		if ("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext)) {
			this.type = JPG;
		} else if ("bmp".equalsIgnoreCase(ext)) {
			this.type = BMP;
		} else if ("pict".equalsIgnoreCase(ext)) {
			this.type = PICT;
		} else if ("png".equalsIgnoreCase(ext)) {
			this.type = PNG;
		} else {
			throw new ExportException(
					"not found any file picture type from the file:"
							+ f.getAbsolutePath());
		}
	}

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * @param inputStream
	 *            the inputStream to set
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		if (!this.hasType(type)) {
			throw new ExportException("not found any type from your support!");
		}
		this.type = type;
	}

	/**
	 * <p>
	 * Discription:将提供的编码文件类型转换为文字类型
	 * </p>
	 * 
	 * @return
	 */
	public String getStringType() {
		String ext = "";
		switch (type) {
		case BMP:
			ext = "bmp";
			break;
		case JPG:
			ext = "jpg";
			break;
		case PICT:
			ext = "pict";
			break;
		case PNG:
			ext = "png";
			break;
		default:
			break;
		}
		return ext;
	}

	/**
	 * @return the chart
	 */
	public IChart getChart() {
		return chart;
	}

	/**
	 * @param chart
	 *            the chart to set
	 */
	public void setChart(IChart chart) {
		this.chart = chart;
		this.setType(JPG);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * @see PictureType#hasType(int)
	 */
	public boolean hasType(int type) {
		return type == BMP || type == JPG || type == PICT || type == PNG;
	}
}
