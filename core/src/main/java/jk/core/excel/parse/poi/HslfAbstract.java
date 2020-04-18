package jk.core.excel.parse.poi;

import jk.core.Excel;
import jk.core.excel.parse.base.Header;
import jk.core.excel.parse.event.ParseSheetListener;
import jk.core.ex.ExcelParseException;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: parse less old excel 2005
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @author: Jack.Lee
 * @version: v1.0
 */
public abstract class HslfAbstract  implements HSSFListener {
	public static final Logger logger = Logger.getLogger(HslfAbstract.class);

	private int minColumns;

	private POIFSFileSystem fs;

	private int lastRowNumber;

	private int lastColumnNumber;

	private boolean outputFormulaValues = true;

	private SheetRecordCollectingListener workbookBuildingListener;

	private HSSFWorkbook stubWorkbook;

	private SSTRecord sstRecord;

	private FormatTrackingHSSFListener formatListener;

	private int sheetIndex = -1;

	private int sheetCounter = -1;

	private BoundSheetRecord[] orderedBSRs;

	@SuppressWarnings("unchecked")
	private ArrayList boundSheetRecords = new ArrayList();

	private int nextRow;

	private int nextColumn;

	private boolean outputNextStringRecord;

	private int curRow;

	/**
	 * 当前sheet中，总数据行数
	 */
//	private int curRows;

	private List<String> rowlist;

	private boolean doSelectSheetIndex = false;

	private String sheetName;

	private int origRow = -1;

	/**
	 * 是否传递解析
	 */
	private boolean doParse;
	// excel中日期保存格式
	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	// 传入参数
	private int titleRow;

	private List<String> parseSheetNames;

	public HslfAbstract(POIFSFileSystem fs) throws SQLException {
		this.fs = fs;
		this.minColumns = -1;
		this.curRow = 0;
		this.rowlist = new ArrayList<String>();
	}

	public HslfAbstract(String filename) throws IOException
			, SQLException {
		this(new POIFSFileSystem(new FileInputStream(filename)));
	}

	// excel记录行操作方法，以sheet索引，行索引和行元素列表为参数，对sheet的一行元素进行操作，元素为String类型
	public abstract void optRows(int sheetIndex, String sheetName, int curRow,
                                 List<String> rowList) throws Exception;

	/**
	 * 遍历 excel 文件
	 */
	public void process() throws Exception {
		process(null);
	}

	public void setParseSheetNames(List<String> sheetNames){
		this.parseSheetNames = sheetNames;
	}

	public void process(Integer sheetIndex) throws Exception {
		MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(
				this);
		formatListener = new FormatTrackingHSSFListener(listener);
		HSSFEventFactory factory = new HSSFEventFactory();
		HSSFRequest request = new HSSFRequest();
		if (sheetIndex != null) {
			doSelectSheetIndex = true;
			this.sheetIndex = sheetIndex - 1;
		}
		if (sheetIndex != null && sheetIndex < 0) {
			throw new RuntimeException("The SheetIndex out of the bounds");
		}
		if (outputFormulaValues) {
			request.addListenerForAllRecords(formatListener);
		} else {
			workbookBuildingListener = new SheetRecordCollectingListener(
					formatListener);
			request.addListenerForAllRecords(workbookBuildingListener);
		}

		factory.processWorkbookEvents(request, fs);
	}

	/**
	 * HSSFListener 监听方法，处理 Record
	 */
	@SuppressWarnings("unchecked")
	public void processRecord(Record record) {
		int thisRow = -1;
		int thisColumn = -1;
		String value = null;
		switch (record.getSid()) {
		case BoundSheetRecord.sid:
			boundSheetRecords.add(record);
			break;
		case BOFRecord.sid:
			BOFRecord br = (BOFRecord) record;
			if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
				if (workbookBuildingListener != null && stubWorkbook == null) {
					stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
				}
				if (orderedBSRs == null) {
					orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
				}
				sheetCounter++;
				if (!doSelectSheetIndex) {
					sheetIndex = sheetCounter;
				}
				String _sheetName = null;
				if (stubWorkbook != null) {
					_sheetName = stubWorkbook.getSheetName(sheetIndex);
					logger.info("over sheetName:" + _sheetName);
				} else if (orderedBSRs != null) {
					_sheetName = orderedBSRs[sheetIndex].getSheetname();
					logger.info("over sheetName:" + _sheetName);
				}
				this.curRow = 0;

				if(_sheetName != null){
					_sheetName = _sheetName.trim();
					doParse = getDoParse(_sheetName);
					//多个sheet同时解析时有效
					endListener(_sheetName, false);
					sheetName = _sheetName;
					sendParseSheetListener(Excel.START, _sheetName);
				}
			}
			break;

		case SSTRecord.sid:
			sstRecord = (SSTRecord) record;
			break;

		case BlankRecord.sid:
			BlankRecord brec = (BlankRecord) record;
			thisRow = brec.getRow();
			thisColumn = brec.getColumn();
			addValue(thisColumn, null);
			break;
		case BoolErrRecord.sid:
			BoolErrRecord berec = (BoolErrRecord) record;
			thisRow = berec.getRow();
			thisColumn = berec.getColumn();
			addValue(thisColumn, null);
			break;

		case FormulaRecord.sid:
			FormulaRecord frec = (FormulaRecord) record;
			thisRow = frec.getRow();
			thisColumn = frec.getColumn();
			if (outputFormulaValues) {
				int rs = frec.getCachedResultType();
				boolean has = frec.hasCachedResultString();
				// if (Double.isNaN(frec.getValue())) {
				if (has) {
					// Formula result is a string
					// This is stored in the next record
					outputNextStringRecord = true;
					nextRow = frec.getRow();
					nextColumn = frec.getColumn();
				} else {
					value = formatListener.formatNumberDateCell(frec);
				}
			} else {
				value = '"' + HSSFFormulaParser.toFormulaString(stubWorkbook,frec.getParsedExpression()) + '"';
			}
			if (!outputNextStringRecord) {
				addValue(thisColumn, value);
			}
			break;
		case StringRecord.sid:
			if (outputNextStringRecord) {
				thisRow = nextRow;
				thisColumn = nextColumn;
				outputNextStringRecord = false;
				StringRecord srec = (StringRecord) record;
				value = srec.getString();
				addValue(thisColumn, value);
			}
			break;

		case LabelRecord.sid:
			LabelRecord lrec = (LabelRecord) record;
			curRow = thisRow = lrec.getRow() + 1;
			thisColumn = lrec.getColumn();
			value = lrec.getValue();
			value = (value == null || value.equals("") ) ? " " : value;
			addValue(thisColumn, value);
			break;
		case LabelSSTRecord.sid:
			LabelSSTRecord lsrec = (LabelSSTRecord) record;

			curRow = thisRow = lsrec.getRow() + 1;
			thisColumn = lsrec.getColumn();
			if (sstRecord == null) {
				rowlist.add(thisColumn, null);
			} else {
				value = sstRecord.getString(lsrec.getSSTIndex()).toString()
						.trim();
				value = value.equals("") ? null : value;
				addValue(thisColumn, value);
			}
			break;
		case NoteRecord.sid:
			NoteRecord nrec = (NoteRecord) record;
			thisRow = nrec.getRow();
			thisColumn = nrec.getColumn();
			break;
		case NumberRecord.sid:
			NumberRecord numrec = (NumberRecord) record;
			curRow = thisRow = numrec.getRow() + 1;
			thisColumn = numrec.getColumn();
			// 判断是否是日期格式,如果是日期则返回yyyy-MM-dd HH:mm:ss格式字符串
			double cellVlaue = numrec.getValue();
			if (DateUtil.isADateFormat(formatListener.getFormatIndex(numrec),
					formatListener.getFormatString(numrec))
					&& DateUtil.isValidExcelDate(cellVlaue)) {
				value = sdf.format(DateUtil.getJavaDate(cellVlaue));
			} else {
				value = formatListener.formatNumberDateCell(numrec).trim();
			}
			value = (value==null || value.equals("")) ? null : value;

			addValue(thisColumn, value);
			break;
		case RKRecord.sid:
			RKRecord rkrec = (RKRecord) record;
			thisRow = rkrec.getRow();
			thisColumn = rkrec.getColumn();
			break;
		default:
			// logger.info(record.getClass() + "-->" + record.getSid());
			break;
		}

		// 遇到新行的操作
		if (thisRow != -1 && thisRow != lastRowNumber) {
			lastColumnNumber = -1;
		}

		// 空值的操作
		if (record instanceof MissingCellDummyRecord) {
			MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
			curRow = thisRow = mc.getRow() + 1;
			thisColumn = mc.getColumn();
			for (int i = rowlist.size(); i < thisColumn; i++) {
				addValue(i, null);
			}
		}

		// 更新行和列的值
		if (thisRow > -1)
			lastRowNumber = thisRow;
		if (thisColumn > -1)
			lastColumnNumber = thisColumn;


		// 行结束时的操作
		if (record instanceof LastCellOfRowDummyRecord) {
			if (minColumns > 0) {
				// 列值重新置空
				if (lastColumnNumber == -1) {
					lastColumnNumber = 0;
				}
			}
			// 行结束时， 调用 optRows() 方法
			lastColumnNumber = -1;
			try {
				if (sheetCounter == sheetIndex && origRow != curRow) {

					if(doParse) {
						optRows(sheetIndex, sheetName, curRow, rowlist);
					}
					origRow = curRow;
				}
			} catch (Exception e) {
				logger.fatal(e.getMessage(), e);
				throw new ExcelParseException(e.getMessage(), e);
			}finally {
				rowlist.clear();
			}

		}
	}

	protected void endListener(String _sheetName, boolean close){
		if(close || (sheetName != null && _sheetName != null && !sheetName.equalsIgnoreCase(_sheetName))){
			if(!hasData()){
				//如果没有数据，且已经解析结束，则发送事件
				sendParseSheetListener(Excel.END_HEADER,sheetName);
			}
			//excel解析结束
			sendParseSheetListener(Excel.END,sheetName);
		}
	}

	private boolean getDoParse(String sheetName){
		boolean doParse = false;
		if(parseSheetNames != null ){
			if(parseSheetNames.contains(sheetName)){
				doParse = true;
			}else{
				doParse = false;
			}

		}else{
			doParse = true;
		}
		return doParse;
	}

	private void addValue(int index, String value) {
		if (index > rowlist.size()) {
			for (int i = rowlist.size(); i < index; i++) {
				rowlist.add(i, null);
			}
		}
		rowlist.add(index, value);
	}

	public void close() throws IOException {
		//处理最后一个sheet
		endListener(null,true);

		if(stubWorkbook != null)
			stubWorkbook.close();

	}

	public int getTitleRow() {
		return titleRow;
	}

	public void setTitleRow(int titleRow) {
		this.titleRow = titleRow;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}


	public void sendParseSheetListener(String type, String sheetName){
		if(getParseSheetListener() != null){
			if(!getDoParse(sheetName)){
				//如果不做解析，则结束通知
				return;
			}
			//开始解析某个sheet数据
			if(Excel.START.equalsIgnoreCase(type)){
				getParseSheetListener().startParseSheet(sheetName);
			}else if(Excel.END_HEADER.equalsIgnoreCase(type)){
				getParseSheetListener().endParseHeaders(sheetName,getHeaders());
			}else if(Excel.END.equalsIgnoreCase(type)){
				getParseSheetListener().endParseSheet(sheetName,getHeaders(),getDataList(sheetName),hasData(),getRows(sheetName));
			}
		}
	}

	/**
	 * 是否有数据，由子类覆盖提供
	 * @return
	 */
	protected boolean hasData() {
		return false;
	}

	/***
	 * 返回当前sheet的头部数据，由子类覆盖提供
	 * @return
	 */
	protected List<Header> getHeaders() {
		return null;
	}

	/**
	 * 返回当前sheet数据，由子类覆盖提供
	 * @return
	 */
	protected List getDataList(String sheetName) {
		return null;
	}

	/**
	 * 提供是否有sheet解析过程的监听器，由子类覆盖
	 * @return
	 */
	protected ParseSheetListener getParseSheetListener(){
		return  null;
	}

	public int getRows(String sheetName) {
		return 0;
	}
}
