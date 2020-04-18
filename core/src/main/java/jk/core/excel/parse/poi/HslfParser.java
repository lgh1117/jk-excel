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
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: HslfParser.java
 * @author: Jack.Lee
 * @version: v1.0
 */
public class HslfParser extends HslfAbstract implements Excel {
	private static final Logger logger = Logger.getLogger(HslfParser.class);

	private ParseInfo parseInfo;

	private Map<String, List<Map>> dataMap;

	private Map<Integer, Header> headerMap;

	private int localSheetIndex = -1;

	private List<String> localSheets = new ArrayList<String>();

	private boolean calParser = true;

	private boolean onlyParse = false;

	private boolean _hasData = false;

	private Map<String, List<String>> existMap = null;

	public HslfParser(ParseInfo parseInfo) throws FileNotFoundException,
            IOException, SQLException {
		super(parseInfo.getFile().getAbsolutePath());
		this.parseInfo = parseInfo;
	}

	/**
	 * @see HslfAbstract#optRows(int, String, int,
	 *      List)
	 */
	public void optRows(int sheetIndex, String sheetName, int curRow,
                        List<String> rowList) throws Exception {

		sheetName = sheetName.trim();
		if (sheetIndex != localSheetIndex) {
			localSheets.add(sheetName);
			localSheetIndex = sheetIndex;
		}
		if (parseInfo.getDataIndex() > curRow) {
			// 如果有头部信息
			parseHeaders(sheetName,rowList);
			if(parseInfo.getDataIndex() == (curRow + 1)) {
				//重置头部
				resetHeader(sheetName);
			}
		} else if (parseInfo.getDataIndex() <= curRow) {
			if(!calParser){
				return;
			}
			if (headerMap == null) {
				logger.warn("找不到映射头部信息,for sheetname:::::"+sheetName);
				calParser = false;
				return;
			}

			initDataMap(sheetName);

			Map<String, String> map = new HashMap<String, String>();
			readValues(map,rowList);

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

	// 如果有头部信息
	private void parseHeaders(String sheetName, List<String> rowList) {
		ParserUtil.parseHeader(sheetName,parseInfo, rowList);
		headerMap = null;
		calParser = true;
		_hasData = false;
	}

	private void initDataMap(String sheetName) {
		if (dataMap == null) {
			dataMap = new HashMap<String, List<Map>>();
		}
		if (!dataMap.containsKey(sheetName)) {
			List<Map> m = new LinkedList<Map>();
			dataMap.put(sheetName, m);
		}
	}

	/**
	 * @see Excel#parseToList(Class)
	 */
	public <T> List<T> parseToList(Class<T> t) {
		try {
			this.process(1);
			if (dataMap == null) {
				logger.warn("the file "
						+ parseInfo.getFile().getName() + " is empty for the first sheet with the headers"
				);
				return Collections.emptyList();
			}
			Iterator<List<Map>> iter = dataMap.values().iterator();
			if(!iter.hasNext()){
				return Collections.emptyList();
			}
			List<Map> data = iter.next();
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
				logger.warn("the file "
						+ parseInfo.getFile().getName() + " is empty for the first sheet with the headers"
				);
				return Collections.emptyList();
			}
			Iterator<List<Map>> iter = dataMap.values().iterator();
			if(!iter.hasNext()){
				return Collections.emptyList();
			}
			return iter.next();
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
			setParseSheetNames(parseInfo.getSheets());
			this.process();
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

	@Override
	public void parse() {
		try {
			onlyParse = true;
			setParseSheetNames(parseInfo.getSheets());
			this.process();
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

	@Override
	protected boolean hasData() {
		return _hasData;
	}

	@Override
	protected List<Header> getHeaders() {
		return parseInfo.getHeaders();
	}

	@Override
	protected List getDataList(String sheetName) {
		return dataMap != null && dataMap.containsKey(sheetName) ? dataMap.get(sheetName) : null;
	}

	@Override
	protected ParseSheetListener getParseSheetListener() {
		return parseInfo.getParseSheetListener();
	}

	@Override
	public int getRows(String sheetName) {
		return dataMap != null && dataMap.containsKey(sheetName) ? dataMap.get(sheetName).size() : 0;
	}

	private void resetHeader(String sheetName) {
		if (headerMap == null) {
			headerMap = ParserUtil.toMap(parseInfo.getHeaders());
			ParserUtil.checkHeaderInfos(parseInfo,sheetName);
			sendParseSheetListener(END_HEADER,sheetName);
			_hasData = true;
		}
	}

	private void readValues(Map<String, String> map, List<String> rowList) {
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
}
