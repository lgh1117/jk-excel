/**
 * 
 */
package jk.core.hd;

import jk.core.excel.parse.base.Header;

import java.util.List;
import java.util.Map;

/**
 * 处理每行数据，提供两个方法，一个方法是处理原始行数据，从excel解析出来的，
 * 一个方法是处理转换后的行数据
 * @author Jack.Lee
 */
public interface RowDataHandle {

	/**
	 * <p>
	 * Discription:行解析处理接口调用,返回处理结果，存放在返回解析的map中,
	 * key为 @CellDataHandle.INFO_NAME
	 * 如果返回的数据封装在一个实体类中，则提示信息不返回
	 * </p>
	 * @param sheetName
	 * 			对应的sheet名称
	 * @param rowIndex
	 *            第几行
	 * @param row
	 *            当前行数据
	 * @param headers
	 *            所有列头信息
	 * @param extras
	 *            存放外部信息
	 * @return
	 */
	String exceute(String sheetName, int rowIndex, Map row, List<Header> headers, Map<String, Object> extras);

	/**
	 * 每一行的提示信息key
	 */
	public static String INFO_NAME = "_rowInfo_";



}
