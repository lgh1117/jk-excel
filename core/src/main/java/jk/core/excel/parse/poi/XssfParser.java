package jk.core.excel.parse.poi;

import jk.core.Excel;
import jk.core.excel.parse.base.Header;
import jk.core.excel.parse.base.ParseInfo;
import jk.core.excel.parse.base.ParserUtil;
import jk.core.excel.parse.event.ParseSheetListener;
import jk.core.hd.CellDataHandle;
import jk.core.hd.ExcelCommonHandle;
import jk.core.hd.RowDataHandle;
import jk.core.util.RegExpUtil;
import jk.core.ex.ExcelParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.util.*;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: XssfParser.java
 * @author: liguohui lgh1177@126.com
 * @version: v1.0
 */
public class XssfParser extends XssfAbstract implements Excel {
	public static final Logger logger = LogManager.getLogger(XssfParser.class);

	private ParseInfo parseInfo;

	private Map<String, List<Map>> dataMap;

	private Map<Integer, Header> headerMap;

	private Map<String, List<String>> existMap = null;

	/**
	 * 是否继续解析
	 */
	private boolean calParser = true;

	private boolean onlyParse = false;

	/**
	 * 是否有数据
	 */
	private boolean _hasData = false;

	/**
	 * @param parseInfo
	 */
	public XssfParser(ParseInfo parseInfo) throws InvalidFormatException, IOException, XmlException {
		super(parseInfo.getFile().getAbsolutePath());
		this.parseInfo = parseInfo;
		this.setParseSheetNames(parseInfo.getSheets());
	}

	/**
	 * @see XssfAbstract#optRows(int, String, int,
	 *      List)
	 */
	public void optRows(int sheetIndex, String sheetName, int curRow,
                        List<String> rowList) throws Exception {

		if (parseInfo.getDataIndex() > curRow) {
			// 如果有头部信息
			parseHeaders(sheetName,rowList);
			if(parseInfo.getDataIndex() == (curRow + 1)) {
				//重置头部
				resetHeader(sheetName);
			}
			if(parseInfo.getExtraCellDataHandle() != null){
				for(int i = 0  ; i < rowList.size() ; i++) {
					parseInfo.getExtraCellDataHandle().optRows(sheetIndex, sheetName, curRow,i,rowList.get(i),parseInfo.getExtras());
				}
			}
		} else if (parseInfo.getDataIndex() <= curRow) {
			if(!calParser){
				//没有解析出头部来，是一个空的sheet
				return;
			}
			if (headerMap == null) {
				logger.warn("找不到映射头部信息,for sheetname:::::"+sheetName);
				calParser = false;
				return;
			}

			//初始化datamap数据集
			initDataMap(sheetName);

			Map<String, String> map = new HashMap<String, String>();
			//读取行有效值
			readValues(rowList,map);

			if (map.size() > 0 && !RegExpUtil.isEmpty(map)) {
				map.put(CellDataHandle.ROW_NUMBER_NAME, String.valueOf(curRow));
				Map rs = new HashMap();
				rs.putAll(map);

				//处理每行事件
				ExcelCommonHandle.cellHandle(curRow,map,rs,parseInfo,getExistList(sheetName));

				///////对所有sheet共享的handler类
				ExcelCommonHandle.rowHandle(parseInfo.getRowDataHandles(),sheetName,curRow,rs,map,parseInfo);

				//仅对特定sheet有效的handler类
				if(parseInfo.getRowHandleMap() != null && parseInfo.getRowHandleMap().size() > 0 ){
					List<RowDataHandle> rowDataHandles = parseInfo.getRowHandleMap().get(sheetName);
					ExcelCommonHandle.rowHandle(rowDataHandles,sheetName,curRow,rs,map,parseInfo);
				}


				///////只做解析，并把解析结果通知使用者，不做数据结果存储
				ExcelCommonHandle.sendParseResult(sheetName,curRow,rs,parseInfo,onlyParse);

				//发送行处理结果监听
				ExcelCommonHandle.sendEndLineParseResult(parseInfo,sheetName,curRow,rs);

				if(!onlyParse){
					dataMap.get(sheetName).add(rs);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("sheetName->" + sheetName + "\tcurRow->" + curRow
					+ "\trow->" + rowList);
		}
	}

	/**
	 * 读取行有效值
	 * @param rowList
	 * @param map
	 */
	private void readValues(List<String> rowList, Map<String, String> map) {
		int index = 0;
		for (String val : rowList) {
			Header h = headerMap.get(index);
			index++;
			if (h == null) {
				continue;
			}
			map.put(h.getName(), val);
		}
	}

	/**
	 * 初始化数据集，给返回集用
	 * @param sheetName
	 */
	private void initDataMap(String sheetName) {
		if (dataMap == null) {
			dataMap = new HashMap<String, List<Map>>();
		}

		if (!dataMap.containsKey(sheetName)) {
			List<Map> m = new LinkedList<Map>();
			dataMap.put(sheetName, m);
			_hasData = true;//重置是否有数据标记
		}
	}

	private void resetHeader(String sheetName) {
		if (headerMap == null) {
			headerMap = ParserUtil.toMap(parseInfo.getHeaders());
			ParserUtil.checkHeaderInfos(parseInfo,sheetName);
			//说明头部解析结束，需要发送一次指令，如果没有数据，则由父类发送指令
			sendParseSheetListener(Excel.END_HEADER);
		}
	}

	// 如果有头部信息
	private void parseHeaders(String sheetName, List<String> rowList) {
		ParserUtil.parseHeader(sheetName,parseInfo, rowList);
		headerMap = null;
		calParser = true;
		_hasData = false;
	}

	/**
	 * @see Excel#parseToList(Class)
	 */
	public <T> List<T> parseToList(Class<T> t) {
		try {
			this.process(1);
			if (dataMap == null) {
				logger.warn("the file " + parseInfo.getFile().getName() + " is empty for the first sheet with the headers"
				);
				return Collections.emptyList();
			}
			List<Map> data = dataMap.values().iterator().next();
			return ParserUtil.convertData(data, t);
		} catch (Exception e) {
			throw new ExcelParseException(e.getMessage(), e);
		}finally {
			try {
				close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				throw new ExcelParseException(e.getMessage(), e);
			}
		}
	}

	/**
	 * @see Excel#parseToMapList()
	 */
	public List<Map> parseToMapList() {
		try {
			this.process(1);
			if (dataMap == null) {
				logger.warn("the file " + parseInfo.getFile().getName() + " is empty for the first sheet with the headers" );
				return Collections.emptyList();
			}
			return dataMap.values().iterator().next();
		} catch (Exception e) {
			throw new ExcelParseException(e.getMessage(), e);
		}finally {
			try {
				close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				throw new ExcelParseException(e.getMessage(), e);
			}
		}
	}

	/**
	 * @see Excel#parseAllSheetToMapList()
	 */
	public Map<String, List<Map>> parseAllSheetToMapList() {
		try {
			if (parseInfo.getSheets() != null && parseInfo.getSheets().size() > 0) {
				int index = 1;

				List<String> sheets = this.getSheetNames();
				for (String sheetName : sheets) {
					if (sheetName == null) {
						continue;
					}
					sheetName = sheetName.trim();
					if (parseInfo.getSheets().contains(sheetName)) {
						this.process(index);
					}
					index++;
				}

			} else {
				this.process();
			}
			return dataMap;
		} catch (Exception e) {
			throw new ExcelParseException(e.getMessage(), e);
		}finally {
			try {
				close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				throw new ExcelParseException(e.getMessage(), e);
			}
		}
	}

	public void parse() {
		try {
			onlyParse = true;
			if (parseInfo.getSheets() != null
					&& parseInfo.getSheets().size() > 0) {
				int index = 1;
				List<String> sheets = this.getSheetNames();
				for (String sheetName : sheets) {
					if (sheetName == null) {
						continue;
					}
					sheetName = sheetName.trim();
					if (parseInfo.getSheets().contains(sheetName)) {
						this.process(index);
					}
					index++;
				}

			} else {
				this.process();
			}
		} catch (Exception e) {
			throw new ExcelParseException(e.getMessage(), e);
		}finally {
			try {
				close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				throw new ExcelParseException(e.getMessage(), e);
			}
		}
	}
	private List<String> getExistList(String sheetName) {
		if(existMap == null){
			existMap = new HashMap<>();
		}
		if (!existMap.containsKey(sheetName)) {
			List<String> list = new ArrayList<>();
			existMap.put(sheetName,list);
		}
		return existMap.get(sheetName);
	}
	@Override
	protected boolean hasData() {
		return _hasData;
	}

	@Override
	protected List<Header> getHeaders() {
		return parseInfo.getHeaders();
	}

	@Override
	protected int getRows(String sheetName) {
		return dataMap != null && dataMap.containsKey(sheetName) ? dataMap.get(sheetName).size() : 0;
	}

	@Override
	protected List getDataList(String sheetName) {
		return dataMap != null && dataMap.containsKey(sheetName) ? dataMap.get(sheetName) : null;
	}

	@Override
	protected ParseSheetListener getParseSheetListener() {
		return  parseInfo.getParseSheetListener();
	}
}
