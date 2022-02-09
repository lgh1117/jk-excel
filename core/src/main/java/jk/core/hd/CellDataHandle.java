package jk.core.hd;

import jk.core.excel.parse.base.Header;

import java.util.List;
import java.util.Map;

/**
 * 处理每个单元格时，对原始单元格诗句进行操作的处理类
 * 
 * @author: liguohui lgh1177@126.com
 * @version: v1.0
 */
public interface CellDataHandle {
	/**
	 * 处理每个单元回调的方法
	 * 
	 * @param rowIndex
	 *            第几行
	 * @param value
	 *            当前行正在处理的单元数据
	 * @param row
	 *            当前行数据
	 * @param header
	 *            当前单元对应的列头信息
	 * @param info
	 *            相关提示信息存放处，对应存放在@CellDataHandle.INFO_NAME中
	 * @param extras
	 *            存放外部信息
	 * @param existList
	 *            验证重复信息,一个sheet维护一个
	 * @return
	 */
	Object exceute(int rowIndex, String value, Map row, Header header,
                   StringBuffer info, Map<String, Object> extras,
                   List<String> existList);

	/**
	 * 每一行的提示信息key
	 */
	String INFO_NAME = "_info_";

	/**
	 * 数据所处行号的key值
	 */
	String ROW_NUMBER_NAME = "_row";
}
