/**
 * 负责提供数据源给workbook，由workbook分批生成sheet数据
 */
package jk.core.excel.gen;


import java.util.List;


/**
 * @author Jack lee
 *
 */
public interface Datasource {
	/**
	 * 提供的数据接口
	 * 
	 * @param sheetConfig
	 *            excel配置文件
	 * @param rows
	 *            同一个sheet中，当前共提供了多少数据。
	 * @return
	 */
	List loadData(SheetConfig sheetConfig, int rows);

	/**
	 * 判断是否还有下一批数据
	 * 
	 * @return
	 */
	boolean hasNext();

}
