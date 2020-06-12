package jk.core.excel.gen;

import jk.core.excel.cellinfo.NavCellInfo;
import jk.core.ex.ExcelParseException;
import l.jk.json.JSONArray;
import l.jk.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: 生成excel时的数据封装和相关配置信息,一个该对象代表一个excel的sheet内容
 * </p>
 * 
 * @file: Config.java
 * @author: Jack lee
 * @version: v1.0
 */
public class SheetConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8578187372406334899L;

	/**
	 * sheet的名字
	 */
	private String sheetName;

	/**
	 * 头部行高
	 */
	private float headRowHeight;

	/**
	 * 数据区域行高
	 */
	private float dataRowHeight;


	/**
	 * 
	 */
	private List<ExpHeader> headers;

	/**
	 * 内容数据
	 */
	private List datas;

	/**
	 * 头部映射关系
	 */
	private File headerConfigFile;

	/**
	 * 需要插入到excel中的图片文件路径
	 */
	private List<Picture> pictures;

	/**
	 * 导数的数据源，如果提供此数据源，则datas无效
	 */
	private Datasource datasource;

	/**
	 * sheet头部补充信息说明，非必须
	 */
	private NavCellInfo navCellInfo;

	/**
	 * @param sheetName
	 *            sheet的显示名称
	 * @param headers
	 *            excel表头
	 * @param datas
	 *            excel填充的数据
	 */
	public SheetConfig(String sheetName, List<ExpHeader> headers,
					   List datas) {
		super();
		this.sheetName = sheetName;
		this.headers = headers;
		this.datas = datas;
	}

	/**
	 *
	 * @param sheetName sheet的显示名称
	 * @param headerConfigFile header配置文件，
	 json格式:[{
		name:"", //每一列的头部显示名称
		valueName:"",//读取数据时取值的key名称
		col:0,//该名称占用几列
		row:0,//该名称占用几行
		rowIndex:0,//第几行,默认为第一行
		colIndex:0,//所处列的位置
		colWidth:0,//列宽
		autoWidth:false//自动按照列头的宽度调整列宽
	}]
	 * @param datas 生成的数据
	 */
	public SheetConfig(String sheetName, File headerConfigFile,
					   List datas) {
		super();
		this.sheetName = sheetName;
		setHeaderConfigFile(headerConfigFile);
		this.datas = datas;
	}

	/**
	 * @param sheetName
	 *            sheet的显示名称
	 * @param headers
	 *            excel表头
	 * @param datasource
	 *            excel填充的数据
	 */
	public SheetConfig(String sheetName, List<ExpHeader> headers,
					   Datasource datasource) {
		super();
		this.sheetName = sheetName;
		this.headers = headers;
		this.datasource = datasource;
	}

	/**
	 * @param sheetName
	 *            sheet的显示名称
	 * @param headerConfigFile
	 *            excel表头 配置文件
	[{
	name:"", //每一列的头部显示名称
	valueName:"",//读取数据时取值的key名称
	col:0,//该名称占用几列
	row:0,//该名称占用几行
	rowIndex:0,//第几行,默认为第一行
	colIndex:0,//所处列的位置
	colWidth:0,//列宽
	autoWidth:false//自动按照列头的宽度调整列宽
	}]
	 * @param datasource
	 *            excel填充的数据
	 */
	public SheetConfig(String sheetName, File headerConfigFile,
					   Datasource datasource) {
		super();
		this.sheetName = sheetName;
		setHeaderConfigFile(headerConfigFile);
		this.datasource = datasource;
	}

	/**
	 * @return the sheetName
	 */
	public String getSheetName() {
		return sheetName;
	}

	/**
	 * @param sheetName
	 *            the sheetName to set
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * @return the headers
	 */
	public List<ExpHeader> getHeaders() {
		return headers;
	}

	/**
	 * @param headers
	 *            the headers to set
	 */
	public void setHeaders(List<ExpHeader> headers) {
		this.headers = headers;
		refreshHeaders();
	}

	/**
	 * @return the datas
	 */
	public List getDatas() {
		return datas;
	}

	/**
	 * @param datas
	 *            the datas to set
	 */
	public void setDatas(List datas) {
		this.datas = datas;
	}

	/**
	 * @return the pictures
	 */
	public List<Picture> getPictures() {
		return pictures;
	}

	/**
	 * @param pictures
	 *            the pictures to set
	 */
	public void setPictures(List<Picture> pictures) {
		this.pictures = pictures;
	}

	public void addPicture(Picture picture) {
		if (this.pictures == null) {
			this.pictures = new ArrayList<Picture>();
		}
		this.pictures.add(picture);
	}

	/**
	 * @return the datasource
	 */
	public Datasource getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource
	 *            the datasource to set
	 */
	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

	public float getHeadRowHeight() {
		return headRowHeight;
	}

	public void setHeadRowHeight(float headRowHeight) {
		this.headRowHeight = headRowHeight;
	}

	public float getDataRowHeight() {
		return dataRowHeight;
	}

	public void setDataRowHeight(float dataRowHeight) {
		this.dataRowHeight = dataRowHeight;
	}

	public File getHeaderConfigFile() {
		return headerConfigFile;
	}

	public void setHeaderConfigFile(File headerConfigFile) {
		this.headerConfigFile = headerConfigFile;
		parseConfigFile();
	}

	public NavCellInfo getNavCellInfo() {
		return navCellInfo;
	}

	public void setNavCellInfo(NavCellInfo navCellInfo) {
		this.navCellInfo = navCellInfo;
//		refreshHeaders();
	}

	private void refreshHeaders() {
		if(this.navCellInfo == null || this.getHeaders() == null || this.getHeaders().size() == 0){
			return;
		}
		if(navCellInfo.getCol() <= 0 || navCellInfo.getRow() <= 0){
			return;
		}
		for(ExpHeader header : this.getHeaders()){
			if(header == null){
				continue;
			}
			header.setRowIndex(header.getRowIndex() + navCellInfo.getRow() + 1);
		}
	}

	private void parseConfigFile() {
		if(headerConfigFile == null){
			return;
		}
		if(!headerConfigFile.exists()){
			throw new ExcelParseException("生成excel头部配置文件不存在:"+headerConfigFile);
		}
		try {
			String configValue = FileUtils.readFileToString(headerConfigFile);
			JSONArray jsonArray = JSONArray.parseArray(configValue);
			int size = jsonArray.size();
			List<ExpHeader> headers = new ArrayList<>();
			for(int i = 0 ; i < size ; i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ExpHeader header = (ExpHeader)JSONObject.toJavaObject(jsonObject, ExpHeader.class);
				headers.add(header);
			}
			if(headers != null && headers.size() > 0){
				this.setHeaders(headers);
			}
		} catch (Exception e) {
			throw new ExcelParseException(e.getMessage()+"--->"+headerConfigFile,e);
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
