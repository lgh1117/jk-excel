package jk.core.excel.parse.base;

import jk.core.hd.CellDataHandle;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义解析excel表头列名称与映射字段关系，以及相关信息
 * @author: Jack Lee
 * @version: v1.0
 */
public class Header {

	/**
	 * 增加sheet模板名称，默认为空
	 */
	private String sheetName;

	/**
	 * 封装名称
	 */
	private String name;

	/**
	 * excel头名称
	 */
	private String excelName;

	/**
	 * 数据下标,从0开始，0表示一个数,如果在csv解析时，首行不是头部信息的，此列必须制定，否则无法正常解析数据或者解析出错,
	 */
	private Integer index;

	/**
	 * 是否正常匹配头部信息
	 */
	private boolean matcher = false;

	/**
	 * 解析时，自动将解析的单位放到此字段中，如果为空，则不存在单位，不支持外部设置单位
	 */
	private String unit;

	private List<CellDataHandle> handles;



	/**
	 * @param name
	 * @param excelName
	 */
	public Header(String name, String excelName) {
		this.name = name;
		this.excelName = excelName;
	}

	/**
	 * @param name
	 * @param excelName
	 * @param index
	 */
	public Header(String name, String excelName, Integer index) {
		this.name = name;
		this.excelName = excelName;
		this.index = index;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the excelName
	 */
	public String getExcelName() {
		return excelName;
	}

	/**
	 * @param excelName
	 *            the excelName to set
	 */
	public void setExcelName(String excelName) {
		this.excelName = excelName;
	}

	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}

	/**
	 * @return the handle
	 */
	public List<CellDataHandle> getHandles() {
		return handles;
	}

	/**
	 * @param handle
	 *            the handle to set
	 */
	public void addHandle(CellDataHandle handle) {
		if(handle == null){
			return;
		}
		if(handles == null){
			handles = new ArrayList<>();
		}
		handles.add(handle);
	}

	public  void addHandles(List<CellDataHandle> hdls){
		if(hdls == null || hdls.isEmpty()){
			return;
		}
		if(handles == null){
			handles = new ArrayList<>();
		}
		handles.addAll(hdls);
	}

	/**
	 * @return 如果返回true，则表示此列头正常匹配，否则未正常匹配
	 */
	public boolean isMatcher() {
		return matcher;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * @param matcher
	 *            the matcher to set
	 */
	public void setMatcher(boolean matcher) {
		this.matcher = matcher;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setHandle(CellDataHandle handle){
		addHandle(handle);
	}

	public CellDataHandle getHandle(){
		if(getHandles() != null){
			return getHandles().get(0);
		}
		return null;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


}
