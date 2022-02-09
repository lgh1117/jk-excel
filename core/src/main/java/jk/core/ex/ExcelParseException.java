package jk.core.ex;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: ExcelParseException.java
 * @author: liguohui lgh1177@126.com
 * @version: v1.0
 */
public class ExcelParseException extends RuntimeException {

	/**
	 * 
	 */
	public ExcelParseException() {
		this("Excel 解析异常");
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ExcelParseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ExcelParseException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ExcelParseException(Throwable cause) {
		super(cause);
	}

}
