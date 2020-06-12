package jk.core.excel.parse.base;

import jk.core.excel.parse.event.ParseListener;
import jk.core.ex.ExcelParseException;
import jk.core.excel.parse.event.ParseSheetListener;
import jk.core.hd.CellDataHandle;
import jk.core.hd.ExtraCellDataHandle;
import jk.core.hd.RowDataHandle;
import l.jk.json.JSONArray;
import l.jk.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 *
 * @file: ParsInfo.java
 * @author: Jack lee
 * @version: v1.0
 */
public class ParseInfo implements Serializable {
	public static final Logger logger = Logger.getLogger(ParseInfo.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 7974757179519382107L;

	public static final String COMMONE_SHEET_NAME = "_common_sheet_name";

	/**
	 * 数据起始行,从0开始，默认第二行,数据起始行之前的都为头部，需要进行数据匹配
	 */
	private int dataIndex = 1;

	/***
	 * excel与表头的映射，如果没有，则从数据行开始解析,返回数据为下标map
	 */
	private List<Mapping> mappings = null;

	/**
	 * 待解析文件，必须
	 */
	private File file;

	private FileType fileType;

	private List<Header> headers;

	private Map<String, List<Header>> sheetHeaderMap;

	/**
	 * 要求所有列必须映射
	 */
	private boolean forceMatcher = false;

	/**
	 * 携带外部信息
	 */
	private Map<String, Object> extras;

	private boolean inited;

	private boolean noHeader = false;

	/**
	 * 是否需要解析excel头部的单位，与Units类共同使用，默认为true，解析；
	 * 在配置mapping时，header的excelName不用带单位，如：excel的excelName为"工程款(万元)"，则Header的ExcelName为"工程款"
	 */
	private boolean parseUnit = true;

	/**
	 * 映射配置文件，如果没有提供映射配置，则可以从文件中进行配置， 文件内容为json格式：
	 * {'returnClass':"结果封装的类，默认为map",'mapping':[ { 'key':'name',
	 * 'value':'名称',"handle":"handle full class
	 * name" },{ 'key':'code', 'value':'编码',"handle":"handle full class name" }
	 * ]}
	 */
	private File mappingFile;

	/**
	 * 需要解析的sheet，默认全部解析
	 */
	private List<String> sheets;

	/**
	 * 行处理器，用来处理每行数据,每个sheet对应一个处理器
	 */
	private Map<String, List<RowDataHandle>> rowHandleMap;

	/**
	 * 所有sheet公用一个处理器
	 */
	private List<RowDataHandle> rowDataHandles;

	/**
	 * 接收分批处理的监听器
	 */
	private ParseListener parseListener;

	/**
	 * 如果解析的文件是csv、tsv文件时，可以传入此分隔符，默认分隔符为","
	 */
	private String csvSeperator = ",";

	/**
	 * 设置标记csv、tsv文件的第一行是否为表头信息，换句话说就是第一行不是数据行，true是，false不是，默认true
	 */
	private boolean csvFirstIsHeader = true;

	/**
	 * 整个文档解析监听器
	 */
	private ParseSheetListener parseSheetListener;

	/**
	 * 除数据列之外的单元格数据处理器
	 */
	private ExtraCellDataHandle extraCellDataHandle;

	/**
	 * @param file
	 *            待解析文件
	 * @param dataIndex
	 *            数据起始行号,从1开始
	 */
	public ParseInfo(File file, int dataIndex) {
		this.dataIndex = dataIndex;
		this.file = file;
	}

	/**
	 *
	 * @param file
	 *            待解析文件
	 * @param dataIndex
	 *            数据起始行号,从1开始
	 * @param forceMatcher
	 *            是否强全匹配，true表示全，如果有未匹配的头部数据，则就抛异常，false不强制，
	 *            但是会在header中记录未匹配的头部信息
	 */
	public ParseInfo(File file, int dataIndex, boolean forceMatcher) {
		this.dataIndex = dataIndex;
		this.file = file;
		this.forceMatcher = forceMatcher;
	}

	/**
	 * @param file
	 *            待解析文件
	 * @param mappingFile
	 *            映射配置文件
	 * @param dataIndex
	 *            数据起始行号,从1开始
	 */
	public ParseInfo(File file, File mappingFile, int dataIndex) {
		this.dataIndex = dataIndex;
		this.mappingFile = mappingFile;
		this.file = file;
	}

	/**
	 * @param file
	 *            待解析文件
	 * @param mappings
	 *            文件与列头映射
	 * @param dataIndex
	 *            数据起始行号,从1开始
	 */
	public ParseInfo(File file, List<Mapping> mappings, int dataIndex) {
		this.dataIndex = dataIndex;
		this.mappings = mappings;
		this.file = file;
	}

	/**
	 * @param file
	 * @param mappings
	 * @param dataIndex
	 * @param fileType
	 */
	public ParseInfo(File file, List<Mapping> mappings, int dataIndex,
                     FileType fileType) {
		this.dataIndex = dataIndex;
		this.mappings = mappings;
		this.file = file;
		this.fileType = fileType;
	}

	/**
	 * @param file
	 * @param mappings
	 * @param dataIndex
	 * @param fileType
	 * @param returnClass
	 */
	public ParseInfo(File file, List<Mapping> mappings, int dataIndex,
                     FileType fileType, Class returnClass) {
		this.dataIndex = dataIndex;
		this.mappings = mappings;
		this.file = file;
		this.fileType = fileType;
		this.returnClass = returnClass;
	}

	/**
	 * 返回数据封装的类，如果没有提供，就封装到map中
	 */
	private Class returnClass;

	public void addSheet(String sheetName) {
		if (this.sheets == null) {
			sheets = new ArrayList<String>();
		}
		sheetName = sheetName.trim();
		sheets.add(sheetName);
	}

	/**
	 * @return the dataIndex
	 */
	public int getDataIndex() {
		return dataIndex;
	}

	/**
	 * @param dataIndex
	 *            the dataIndex to set
	 */
	public void setDataIndex(int dataIndex) {
		this.dataIndex = dataIndex;
	}

	/**
	 * @return the mapping
	 */
	public List<Mapping> getMappings() {
		return mappings;
	}

	/**
	 * @param mappings
	 *            the mapping to set
	 */
	public void setMappings(List<Mapping> mappings) {
		this.mappings = mappings;
	}

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
	}

	/**
	 * @return the fileType
	 */
	public FileType getFileType() {
		return fileType;
	}

	/**
	 * @param fileType
	 *            the fileType to set
	 */
	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the headers
	 */
	public List<Header> getHeaders() {
		return headers;
	}

	/**
	 * @return the inited
	 */
	public boolean isInited() {
		return inited;
	}

	/**
	 * @param inited
	 *            the inited to set
	 */
	public void setInited(boolean inited) {
		this.inited = inited;
	}

	/**
	 * @param headers
	 *            the headers to set
	 */
	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	/**
	 * @return the returnClass
	 */
	public Class getReturnClass() {
		return returnClass;
	}

	/**
	 * @param returnClass
	 *            the returnClass to set
	 */
	public void setReturnClass(Class returnClass) {
		this.returnClass = returnClass;
	}

	/**
	 * @return the sheets
	 */
	public List<String> getSheets() {
		return sheets;
	}

	/**
	 * @param sheets
	 *            the sheets to set
	 */
	public void setSheets(List<String> sheets) {
		this.sheets = sheets;
	}

	/**
	 * @return the mappingFile
	 */
	public File getMappingFile() {
		return mappingFile;
	}

	/**
	 * @param mappingFile
	 *            the mappingFile to set
	 */
	public void setMappingFile(File mappingFile) {
		this.mappingFile = mappingFile;
	}

	/**
	 * 如果解析的文件是csv、tsv文件时，可以传入此分隔符，默认分隔符为","
	 * @return
	 */
	public String getCsvSeperator() {
		return csvSeperator;
	}

	/**
	 * 如果解析的文件是csv、tsv文件时，可以传入此分隔符，默认分隔符为","
	 * @param csvSeperator
	 */
	public void setCsvSeperator(String csvSeperator) {
		this.csvSeperator = csvSeperator;
	}


	/**
	 * 设置标记csv、tsv文件的第一行是否为表头信息，换句话说就是第一行不是数据行，true是，false不是，默认true
	 * @return
	 */
	public boolean isCsvFirstIsHeader() {
		return csvFirstIsHeader;
	}

	/**
	 * 设置标记csv、tsv文件的第一行是否为表头信息，换句话说就是第一行不是数据行，true是，false不是，默认true
	 * @param csvFirstIsHeader
	 */
	public void setCsvFirstIsHeader(boolean csvFirstIsHeader) {
		this.csvFirstIsHeader = csvFirstIsHeader;
	}

	public void init() {
		if (file == null || !file.exists()) {
			throw new ExcelParseException("待解析文件不存在-->" + file);
		}
		long t = System.currentTimeMillis();
		initFileType();
		initHeader();
		logger.info("header init info:"+(System.currentTimeMillis()-t)+"ms");
		this.inited = true;
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 */
	private void initHeader() {
		if (headers == null) {
			headers = new ArrayList<Header>();
		}
		if(sheetHeaderMap == null){
			sheetHeaderMap = new HashMap<>();
		}
		if (mappingFile != null && mappingFile.exists()) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("load excel mapping from file-->"
							+ mappingFile);
				}
				String mapping = FileUtils.readFileToString(mappingFile,
						"UTF-8");
				if (mapping.trim().length() == 0) {
					throw new ExcelParseException("映射文件内容为空-->" + mappingFile);
				}
				mapping = mapping.trim();
				JSONObject map = JSONObject.parseObject(mapping);
				if (map.containsKey("returnClass")) {
					this.returnClass = Class.forName(map
							.getString("returnClass"));
				}
				JSONArray arr = map.getJSONArray("mapping");
				if (arr.size() > 0) {
					String sheetName = null;
					for (Object obj : arr) {
						if (obj == null) {
							continue;
						}
						JSONObject json = (JSONObject) obj;
						Header h = new Header(json.getString("key").trim(),
								json.getString("value").trim());
						if(json.containsKey("sheetName")){
							sheetName = json.getString("sheetName");
							h.setSheetName(sheetName);
						}
						if(json.containsKey("index")){
							try{
								Integer index = Integer.parseInt(json.getString("index"));
								if(index >= 0){
									h.setIndex(index);
								}
							}catch (Exception e){
								//nothing todo
							}
						}
						if (json.containsKey("handle")) {
							String handle = json.getString("handle");
							String[] handles = handle.split(",");
							for(String cls : handles){
								if(!StringUtils.isEmpty(cls)){
									CellDataHandle hle = (CellDataHandle) Class.forName(cls).newInstance();
									h.addHandle(hle);
								}
							}
						}
						addHeader(sheetName,h);
					}
				}
			} catch (IOException e) {
				throw new ExcelParseException("读取映射文件出错-->" + mappingFile, e);
			} catch (ClassNotFoundException e) {
				throw new ExcelParseException("读取映射文件出错,结果封装类不存在-->"
						+ mappingFile, e);
			} catch (InstantiationException e) {
				throw new ExcelParseException("初始化失败-->" + mappingFile, e);
			} catch (IllegalAccessException e) {
				throw new ExcelParseException("初始化失败-->" + mappingFile, e);
			}catch (Exception ex){
				throw new ExcelParseException("初始化失败-->" + mappingFile, ex);
			}
		} else if (mappings != null) {
			String sheetName = null;
			for (Mapping mp : mappings) {
				if (mp != null) {
					sheetName = mp.getSheetName();
					Header h = new Header(mp.getName(), mp.getExcelName());
					if(mp.getIndex() >= 0){
						h.setIndex(mp.getIndex());
					}
					h.addHandles(mp.getHandles());
					h.setSheetName(sheetName);
					addHeader(sheetName,h);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("load excel mapping from programer");
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("not found any mapping configuration!");
			}
			// 如果没有映射，则以列为基准，数据最长的第一列为列头，其余的舍去
			noHeader = true;
		}

	}

	private void addHeader(String sheetName, Header header){
		sheetName = ParseUtils.isEmpty(sheetName) ? COMMONE_SHEET_NAME : sheetName;
		sheetName = sheetName.trim();
		sheetName = sheetName.toLowerCase();
		if(sheetHeaderMap.containsKey(sheetName)){
			sheetHeaderMap.get(sheetName).add(header);
		}else{
			List<Header> _headers = new ArrayList<>();
			_headers.add(header);
			sheetHeaderMap.put(sheetName,_headers);
		}
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 */
	private void initFileType() {
		if (this.fileType != null) {
			return;
		}
		String filename = file.getName().toLowerCase();
		if (filename.endsWith(".xls")) {
			this.fileType = FileType.XLS_DOC;
		} else if (filename.endsWith(".xlsx")) {
			this.fileType = FileType.XLSX;
		}

		if (this.fileType == null) {
			DetectFileType d = new DetectFileType(file);
			this.fileType = d.getType();
		}
	}

	/**
	 * @return the noHeader
	 */
	public boolean isNoHeader() {
		return noHeader;
	}

	/**
	 * @param noHeader
	 *            the noHeader to set
	 */
	public void setNoHeader(boolean noHeader) {
		this.noHeader = noHeader;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * @return the extras
	 */
	public Map<String, Object> getExtras() {
		return extras;
	}

	/**
	 * @param extras
	 *            the extras to set
	 */
	public void setExtras(Map<String, Object> extras) {
		this.extras = extras;
	}

	/**
	 * @return the forceMatcher 是否强全匹配，true表示全，如果有未匹配的头部数据，则就抛异常，false不强制，
	 *         但是会在header中记录未匹配的头部信息
	 */
	public boolean isForceMatcher() {
		return forceMatcher;
	}

	/**
	 * @param forceMatcher
	 *            是否强全匹配，true表示全，如果有未匹配的头部数据，则就抛异常，false不强制，
	 *            但是会在header中记录未匹配的头部信息
	 */
	public void setForceMatcher(boolean forceMatcher) {
		this.forceMatcher = forceMatcher;
	}

	/**
	 * @return the rowHandleMap
	 */
	public Map<String, List<RowDataHandle>> getRowHandleMap() {
		return rowHandleMap;
	}


	public void addRowHandle(String sheetName, RowDataHandle rowDataHandle) {
		if(rowHandleMap == null){
			rowHandleMap = new HashMap<String, List<RowDataHandle>>();
		}
		if(rowHandleMap.containsKey(sheetName)){
			rowHandleMap.get(sheetName).add(rowDataHandle);
		}else{
			List<RowDataHandle> rows = new ArrayList<>();
			rows.add(rowDataHandle);
			rowHandleMap.put(sheetName, rows);
		}

	}

	public void addRowHandles(String sheetName, RowDataHandle ... rowDataHandles) {
		if(rowHandleMap == null){
			rowHandleMap = new HashMap<String, List<RowDataHandle>>();
		}
		if(rowDataHandles != null && rowDataHandles.length > 0){
			for(RowDataHandle hdl : rowDataHandles){
				addRowHandle(sheetName,hdl);
			}
		}
	}

	public void addRowHandles(RowDataHandle ... rowDataHandles) {
		if(rowDataHandles != null && rowDataHandles.length > 0){
			for(RowDataHandle hdl : rowDataHandles){
				setRowDataHandle(hdl);
			}
		}
	}

	public Map<String, List<Header>> getSheetHeaderMap() {
		return sheetHeaderMap;
	}

	/**
	 * @return the rowDataHandle
	 */
	public List<RowDataHandle> getRowDataHandles() {
		return rowDataHandles;
	}

	/**
	 * @param rowDataHandle the rowDataHandle to set
	 */
	public void setRowDataHandle(RowDataHandle rowDataHandle) {
		if(rowDataHandle == null){
			return;
		}
		if(rowDataHandles == null){
			rowDataHandles = new ArrayList<>();
		}
		rowDataHandles.add(rowDataHandle);
	}

	public void setParseListener(ParseListener parseListener) {
		this.parseListener = parseListener;
	}

	public ParseListener getParseListener() {
		return parseListener;
	}

	public ParseSheetListener getParseSheetListener(){
		return parseSheetListener;
	}

	public void setParseSheetListener(ParseSheetListener parseSheetListener) {
		this.parseSheetListener = parseSheetListener;
	}

	public boolean isParseUnit() {
		return parseUnit;
	}

	public void setParseUnit(boolean parseUnit) {
		this.parseUnit = parseUnit;
	}

	public ExtraCellDataHandle getExtraCellDataHandle() {
		return extraCellDataHandle;
	}

	public void setExtraCellDataHandle(ExtraCellDataHandle extraCellDataHandle) {
		this.extraCellDataHandle = extraCellDataHandle;
	}
}
