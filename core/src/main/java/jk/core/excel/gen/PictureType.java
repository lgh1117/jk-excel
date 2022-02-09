package jk.core.excel.gen;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: ExcelPictureType.java
 * @author: liguohui lgh1177@126.com
 * @version: v1.0
 */
public interface PictureType {

	/**
	 * png文件类型图片
	 */
	public static final int PNG = HSSFWorkbook.PICTURE_TYPE_PNG;

	/**
	 * jpg文件类型图片
	 */
	public static final int JPG = HSSFWorkbook.PICTURE_TYPE_JPEG;

	/**
	 * bitmap类型图片
	 */
	public static final int BMP = HSSFWorkbook.PICTURE_TYPE_DIB;

	/**
	 * 苹果图片文件
	 */
	public static final int PICT = HSSFWorkbook.PICTURE_TYPE_PICT;

	boolean hasType(int type);

}
