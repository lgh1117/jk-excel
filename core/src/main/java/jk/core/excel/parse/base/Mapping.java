package jk.core.excel.parse.base;

import jk.core.hd.CellDataHandle;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: Mapping.java
 * @author: Jack lee
 * @version: v1.0
 */
public class Mapping implements Serializable {

	/**
	 * 使用于那个sheet模板，默认为空，如果为空，则使用所有的sheet
	 */
	private String sheetName;

	/**
	 * 封装后的名称
	 */
	private String name;

	/**
	 * excel列头名称
	 */
	private String excelName;

	/**
	 * 数据下标
	 */
	private int index = -1;

	/**
	 * 处理该列数据的接口函数
	 */
	private List<CellDataHandle> handles;

	public Mapping() {
	}

	/**
	 * @param name
	 * @param excelName
	 */
	public Mapping(String name, String excelName) {
		this.name = name;
		this.excelName = excelName;
	}

	/**
	 * 指定excel 中cell的位置，如第几列，从0开始，主要用于csv，tsv文件
	 * @param name 列名
	 * @param excelName excel中的列头名称
	 * @param index 位置
	 */
	public Mapping(String name, String excelName, int index) {
		this.name = name;
		this.excelName = excelName;
		this.index = index;
	}

	/**
	 * 根据sheet名称，列名，excel名称构造函数
	 * @param sheetName sheet模板名称
	 * @param name 列名
	 * @param excelName excel上名称
	 */
	public Mapping(String sheetName, String name, String excelName) {
		this.name = name;
		this.excelName = excelName;
		this.sheetName = sheetName;
	}

	/**
	 * 根据sheet名称，列名，excel名称构造函数
	 * @param sheetName sheet模板名称
	 * @param name 列名
	 * @param excelName excel上名称
	 * @param   handle 单元格触发处理逻辑类
	 */
	public Mapping(String sheetName, String name, String excelName, CellDataHandle handle) {
		this.name = name;
		this.excelName = excelName;
		this.sheetName = sheetName;
		addHandle(handle);
	}

	/**
	 * @param name
	 * @param excelName
	 * @param handle
	 */
	public Mapping(String name, String excelName, CellDataHandle handle) {
		this.name = name;
		this.excelName = excelName;
		addHandle(handle);
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
	 * @return the handles
	 */
	public List<CellDataHandle> getHandles() {
		return handles;
	}

	public void addHandle(CellDataHandle handle){
		if(handles == null){
			handles = new ArrayList<>();
		}
		handles.add(handle);
	}

	public void setHandle(CellDataHandle handle){
		addHandle(handle);
	}

	public void addHandles(CellDataHandle ... hdls){
		if(handles == null){
			handles = new ArrayList<>();
		}
		if(hdls != null && hdls.length > 0){
			for(CellDataHandle h : hdls){
				if(h != null){
					handles.add(h);
				}
			}
		}

	}
	/**
	 * @param handles
	 *            the handle to set
	 */
	public void setHandles(List<CellDataHandle> handles) {
		this.handles = handles;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
