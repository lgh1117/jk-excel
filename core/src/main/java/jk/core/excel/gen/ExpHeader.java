package jk.core.excel.gen;

import jk.core.excel.cellinfo.CellInfo;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: ExcelHeader.java
 * @author: Jack lee
 * @version: v1.0
 */
public class ExpHeader implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4099233841665439130L;

	/**
	 * 每一列的头部显示名称
	 */
	private String name;

	/**
	 * 读取数据时取值的key名称
	 */
	private String valueName;

	/**
	 * 该名称占用几列
	 */
	private int col;

	/**
	 * 该名称占用几行
	 */
	private int row;

	/**
	 * 第几行,默认为第一行
	 */
	private int rowIndex;

	/***
	 * 所处列的位置
	 */
	private int colIndex;

	/**
	 * 列宽
	 */
	private int  colWidth;

	/**
	 * 设置单元格属性，比如颜色，字体
	 */
	private CellInfo cellInfo;
	
	/**
	 * 头部字体，属性，颜色设置
	 */
	private CellInfo headCellInfo;
	
	/**
	 * 单页格式信息
	 */
	private CellStyle cellStyle;
	
	/**
	 * 表示是否做过字体样式渲染，计算过程中使用
	 */
	private boolean genStyle = false;

	/**
	 * 自动按照列头的宽度调整列宽
	 */
	private boolean autoWidth = false;

	/**
	 * 下拉列约束值
	 */
	private List<String> constraintData = null;


	public ExpHeader(){}

	/**
	 * @param name
	 *            表头显示名称
	 * @param valueName
	 *            取值名称
	 * @param col
	 *            共占几列
	 * @param row
	 *            共占几行
	 * @param rowIndex
	 *            第几行，序列从1开始
	 * @param colIndex
	 *            第几列，序列从1开始
	 */
	public ExpHeader(String name, String valueName, int row, int col,
					 int rowIndex, int colIndex) {
		super();
		this.name = name;
		this.valueName = valueName;
		this.col = col;
		this.row = row;
		this.rowIndex = rowIndex - 1;
		this.colIndex = colIndex - 1;
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
	 * @return the col
	 */
	public int getCol() {
		return col;
	}

	/**
	 * @param col
	 *            the col to set
	 */
	public void setCol(int col) {
		this.col = col;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param row
	 *            the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the valueName
	 */
	public String getValueName() {
		return valueName;
	}

	/**
	 * @param valueName
	 *            the valueName to set
	 */
	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	/**
	 * @return the rowIndex
	 */
	public int getRowIndex() {
		return rowIndex;
	}

	/**
	 * @param rowIndex
	 *            the rowIndex to set
	 */
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	/**
	 * @return the colIndex
	 */
	public int getColIndex() {
		return colIndex;
	}

	/**
	 * @param colIndex
	 *            the colIndex to set
	 */
	public void setColIndex(int colIndex) {
		this.colIndex = colIndex;
	}

	/**
	 * @return the cellInfo
	 */
	public CellInfo getCellInfo() {
		return cellInfo;
	}

	/**
	 * @param cellInfo the cellInfo to set
	 */
	public void setCellInfo(CellInfo cellInfo) {
		this.cellInfo = cellInfo;
	}

	/**
	 * @return the cellStyle
	 */
	public CellStyle getCellStyle() {
		return cellStyle;
	}

	/**
	 * @param cellStyle the cellStyle to set
	 */
	public void setCellStyle(CellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}

	
	/**
	 * @return the headCellInfo
	 */
	public CellInfo getHeadCellInfo() {
		return headCellInfo;
	}

	/**
	 * @param headCellInfo the headCellInfo to set
	 */
	public void setHeadCellInfo(CellInfo headCellInfo) {
		this.headCellInfo = headCellInfo;
	}

	/**
	 * @return the genStyle
	 */
	public boolean isGenStyle() {
		return genStyle;
	}

	/**
	 * @param genStyle the genStyle to set
	 */
	public void setGenStyle(boolean genStyle) {
		this.genStyle = genStyle;
	}

	public boolean isAutoWidth() {
		return autoWidth;
	}

	public void setAutoWidth(boolean autoWidth) {
		this.autoWidth = autoWidth;
	}

	public int getColWidth() {
		return colWidth;
	}

	public void setColWidth(int colWidth) {
		this.colWidth = colWidth;
	}

	public List<String> getConstraintData() {
		return constraintData;
	}

	public void setConstraintData(List<String> constraintData) {
		this.constraintData = constraintData;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
