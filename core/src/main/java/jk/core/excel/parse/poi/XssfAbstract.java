package jk.core.excel.parse.poi;

import jk.core.Excel;
import jk.core.excel.parse.base.Header;
import jk.core.excel.parse.event.ParseSheetListener;
import jk.core.ex.ExcelParseException;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.*;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheets;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorkbookDocument;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * read and parse the excel 2007 or later
 * 
 * @author Jack.Lee
 * @since 1.0
 */
public abstract class XssfAbstract extends DefaultHandler {
	public static final Logger logger = Logger.getLogger(XssfAbstract.class);

	/**
	 * These are the different kinds of cells we support. We keep track of the
	 * current one between the start and end.
	 */
	enum XSSFDataType {
		BOOLEAN, ERROR, FORMULA, INLINE_STRING, SST_STRING, NUMBER,
	}

	private SharedStringsTable sst;

	/**
	 * Table with the styles used for formatting
	 */
	private StylesTable stylesTable;

	private SharedStringsTable sharedStringsTable;

	// Set when V start element is seen
	private boolean vIsOpen;

	// Set when F start element is seen
	private boolean fIsOpen;

	// Set when an Inline String "is" is seen
	private boolean isIsOpen;

	// Set when a header/footer element is seen
	private boolean hfIsOpen;

	// Set when cell start element is seen;
	// used when cell close element is seen.
	private XSSFDataType nextDataType;

	// Used to format numeric cell values.
	private short formatIndex;

	private String formatString;

	private final DataFormatter formatter;

	private String cellRef;

	private boolean formulasNotResults;

	/**
	 * excel file name
	 */
	private String fileName;

	private int sheetIndex = -1;

	private String sheetName;

	private int curRow = 0;// current row

	private int curCol = 0; // current column

	private int preCol = 0; // prefix column

	private int titleRow = 0; // title row

	private int rowSize = 0; // all row size

	/**
	 * one row data
	 */
	private List<String> rowList = new ArrayList<String>();

	// Gathers characters as they are seen.
	private StringBuffer value = new StringBuffer();

	private StringBuffer formula = new StringBuffer();

	private StringBuffer headerFooter = new StringBuffer();

	// excel中日期保存格式
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String systemFormatString = "yyyy-MM-dd HH:mm:ss";

	private OPCPackage pkg;

	private List<String> parseSheetNames ;

	/**
	 * Accepts objects needed while parsing.
	 * @throws InvalidFormatException
	 */
	public XssfAbstract(String fileName) throws InvalidFormatException {
		this.fileName = fileName;
		this.formulasNotResults = false;
		this.nextDataType = XSSFDataType.NUMBER;
		formatter = new HSSFDataFormatter();
		this.fileName = fileName;
		pkg = OPCPackage.open(fileName);
	}

	public void setParseSheetNames(List<String> sheetNames){
		this.parseSheetNames = sheetNames;
	}
	public OPCPackage getPkg(){
		return pkg;
	}

	/**
	 * @param sheetIndex
	 * @param curRow
	 * @param rowList
	 * @throws SQLException
	 */
	public abstract void optRows(int sheetIndex, String sheetName, int curRow,
                                 List<String> rowList) throws Exception;

	public void process() throws Exception {
		this.process(null);
	}

	public void process(Integer sId) throws Exception {
		XSSFReader r = new XSSFReader(pkg);
		sst = r.getSharedStringsTable();
		XMLReader parser = getSheetParser();
		this.stylesTable = r.getStylesTable();
		this.sharedStringsTable = r.getSharedStringsTable();
		if (sId != null) {
			curRow = 0;
			sheetIndex = sId;
			InputStream sheet = getSheet(r, sId);
			if (sheet == null) {
				throw new RuntimeException("The SheetIndex out of the bounds");
			}
			sheetName = getSheetName(sId - 1);
			_parser(parser,sheet);
		} else {
			Iterator<InputStream> sheets = r.getSheetsData();
			while (sheets.hasNext()) {
				curRow = 0;
				sheetIndex++;
				sheetName = getSheetName(sheetIndex);
				if(parseSheetNames != null && parseSheetNames.contains(sheetName)){
					_parser(parser,sheets.next());
				}else if(parseSheetNames == null || parseSheetNames.size() == 0){
					_parser(parser,sheets.next());
				}
			}
		}

	}

	private void _parser(XMLReader parser, InputStream sheet) throws IOException, SAXException {
		try {
			sendParseSheetListener(Excel.START);
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
		} finally {
			if(getRows(sheetName) == 0){
				//如果没有数据，则需要执行头部事件
				sendParseSheetListener(Excel.END_HEADER);
			}
			sendParseSheetListener(Excel.END);
			sheet.close();
			logger.info("over sheetName:" + sheetName);
		}
	}

	public void sendParseSheetListener(String type){
		if(getParseSheetListener() != null){
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
	 * 返回当前sheet对应的数据条数，由子类覆盖提供
	 * @return
	 */
	protected int getRows(String sheetName) {
		return -9;
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

	/**
	 * 关闭数据流
	 * @throws IOException
	 */
	public void close() throws IOException {
		if(pkg != null){
			pkg.close();
		}
	}

	/**
	 * @param r
	 * @param sId
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	private InputStream getSheet(XSSFReader r, Integer sId)
			throws InvalidFormatException, IOException {
		PackageRelationship coreDocRelationship = this.pkg
				.getRelationshipsByType(PackageRelationshipTypes.CORE_DOCUMENT)
				.getRelationship(0);

		// Get the part that holds the workbook
		PackagePart workbookPart = this.pkg.getPart(coreDocRelationship);
		PackageRelationship rel = workbookPart.getRelationship("rId" + sId);
		if (rel == null) {
			throw new IllegalArgumentException("No Sheet found with r:id "
					+ sId);
		}

		PackagePartName relName = PackagingURIHelper.createPartName(rel
				.getTargetURI());
		String partName = relName.getName();
		if (partName == null) {
			throw new RuntimeException("Not found relname with r:id" + sId);
		}
		if (partName.toLowerCase().startsWith("/xl/worksheets")) {
			return r.getSheet("rId" + sId);
		} else {
			sId++;
			return getSheet(r, sId);
		}

	}

	public XMLReader getSheetParser() throws SAXException {
		XMLReader parser = XMLReaderFactory
				.createXMLReader("org.apache.xerces.parsers.SAXParser");
		parser.setContentHandler(this);
		return parser;
	}

	private boolean isTextTag(String name) {
		if ("v".equals(name)) {
			// Easy, normal v text tag
			return true;
		}
		if ("inlineStr".equals(name)) {
			// Easy inline string
			return true;
		}
		if ("t".equals(name) && isIsOpen) {
			// Inline string <is><t>...</t></is> pair
			return true;
		}
		// It isn't a text tag
		return false;
	}

	public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
		if (isTextTag(name)) {
			vIsOpen = true;
			// Clear contents cache
			value.setLength(0);
		} else if ("is".equals(name)) {
			// Inline string outer tag
			isIsOpen = true;
		} else if ("f".equals(name)) {
			// Clear contents cache
			formula.setLength(0);

			// Mark us as being a formula if not already
			if (nextDataType == XSSFDataType.NUMBER) {
				nextDataType = XSSFDataType.FORMULA;
			}

			// Decide where to get the formula string from
			String type = attributes.getValue("t");
			if (type != null && type.equals("shared")) {
				// Is it the one that defines the shared, or uses it?
				String ref = attributes.getValue("ref");
				String si = attributes.getValue("si");

				if (ref != null) {
					// This one defines it
					// TODO Save it somewhere
					fIsOpen = true;
				} else {
					// This one uses a shared formula
					// TODO Retrieve the shared formula and tweak it to
					// match the current cell
					if (formulasNotResults) {
						logger.error("Warning - shared formulas not yet supported!");
					} else {
						// It's a shared formula, so we can't get at the formula
						// string yet
						// However, they don't care about the formula string, so
						// that's ok!
					}
				}
			} else {
				fIsOpen = true;
			}
		} else if ("oddHeader".equals(name) || "evenHeader".equals(name)
				|| "firstHeader".equals(name) || "firstFooter".equals(name)
				|| "oddFooter".equals(name) || "evenFooter".equals(name)) {
			hfIsOpen = true;
			// Clear contents cache
			headerFooter.setLength(0);
		} else if ("row".equals(name)) {
			curRow = Integer.parseInt(attributes.getValue("r"));
		}
		// c => cell
		else if ("c".equals(name)) {
			// Set up defaults.
			this.nextDataType = XSSFDataType.NUMBER;
			this.formatIndex = -1;
			this.formatString = null;
			cellRef = attributes.getValue("r");
			String cellType = attributes.getValue("t");
			String cellStyleStr = attributes.getValue("s");
			if ("b".equals(cellType))
				nextDataType = XSSFDataType.BOOLEAN;
			else if ("e".equals(cellType))
				nextDataType = XSSFDataType.ERROR;
			else if ("inlineStr".equals(cellType))
				nextDataType = XSSFDataType.INLINE_STRING;
			else if ("s".equals(cellType))
				nextDataType = XSSFDataType.SST_STRING;
			else if ("str".equals(cellType))
				nextDataType = XSSFDataType.FORMULA;
			else if (cellStyleStr != null) {
				// Number, but almost certainly with a special style or format
				int styleIndex = Integer.parseInt(cellStyleStr);
				XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
				this.formatIndex = style.getDataFormat();
				this.formatString = style.getDataFormatString();
				if (this.formatString == null)
					this.formatString = BuiltinFormats
							.getBuiltinFormat(this.formatIndex);
			}
			curCol = this.getRowIndex(cellRef);
		}
	}

	public void endElement(String uri, String localName, String name)
			throws SAXException {
		String thisStr = null;
		String errorStr = null;

		// v => contents of a cell
		if (isTextTag(name)) {
			vIsOpen = false;

			// Process the value contents as required, now we have it all
			switch (nextDataType) {
			case BOOLEAN:
				char first = value.charAt(0);
				thisStr = first == '0' ? "FALSE" : "TRUE";
				break;

			case ERROR:
				errorStr = "ERROR:" + value.toString();
				break;

			case FORMULA:
				if (formulasNotResults) {
					thisStr = formula.toString();
				} else {
					String fv = value.toString();

					if (this.formatString != null) {
						try {
							// Try to use the value as a formattable number
							double d = Double.parseDouble(fv);
							thisStr = formatter.formatRawCellContents(d,
									this.formatIndex, this.formatString);
						} catch (NumberFormatException e) {
							// Formula is a String result not a Numeric one
							thisStr = fv;
						}
					} else {
						// No formating applied, just do raw value in all cases
						thisStr = fv;
					}
				}
				break;

			case INLINE_STRING:
				// TODO: Can these ever have formatting on them?
				XSSFRichTextString rtsi = new XSSFRichTextString(
						value.toString());
				thisStr = rtsi.toString();
				break;

			case SST_STRING:
				String sstIndex = value.toString();
				try {
					int idx = Integer.parseInt(sstIndex);
					XSSFRichTextString rtss = new XSSFRichTextString(
							sharedStringsTable.getEntryAt(idx));
					thisStr = rtss.toString();
				} catch (NumberFormatException ex) {
					logger.error("Failed to parse SST index '" + sstIndex
							+ "': " + ex.getMessage(),ex);
				}
				break;

			case NUMBER:
				String n = value.toString();
				n = n == null || "".equals(n.trim()) ? null : n.trim();
				if (this.formatString != null) {
					Double d = Double.parseDouble(n);
					if (HSSFDateUtil.isADateFormat(formatIndex, formatString)) {
						if (!isTime()) {
							thisStr = formatter.formatRawCellContents(d,
									this.formatIndex, this.systemFormatString);
						} else {
							thisStr = formatter.formatRawCellContents(d,
									this.formatIndex, this.formatString);
						}
					} else {
						//数字类型进行式化
						thisStr = formatter.formatRawCellContents(d,
								this.formatIndex, this.formatString);
					}
				} else {
					thisStr = n;
				}

				break;

			default:
				errorStr = "(TODO: Unexpected type: " + nextDataType + ")";
				break;
			}
			if (errorStr != null) {
				logger.fatal(errorStr);
				thisStr = null;
			}
			thisStr = (thisStr == null || thisStr.equals("")) ? null : thisStr;
			int cols = curCol - preCol;
			if (cols > 1) {
				for (int i = 0; i < cols - 1; i++) {
					rowList.add(preCol, null);
				}
			}
			preCol = curCol;
			rowList.add(curCol - 1, thisStr);
		} else if ("f".equals(name)) {
			fIsOpen = false;
		} else if ("is".equals(name)) {
			isIsOpen = false;
		} else if ("row".equals(name)) {
			// output.endRow();
			int tmpCols = rowList.size();
			if (curRow > this.titleRow && tmpCols < this.rowSize) {
				for (int i = 0; i < this.rowSize - tmpCols; i++) {
					rowList.add(rowList.size(), null);
				}
			}
			try {
				optRows(sheetIndex, sheetName, curRow, rowList);
			} catch (Exception e) {
				logger.fatal(e.getMessage(), e);
				throw new ExcelParseException(e.getMessage(), e);
			}
			if (curRow == this.titleRow) {
				this.rowSize = rowList.size();
			}
			rowList.clear();
			curCol = 0;
			preCol = 0;
		} else if ("oddHeader".equals(name) || "evenHeader".equals(name)
				|| "firstHeader".equals(name)) {
			hfIsOpen = false;
			// output.headerFooter(headerFooter.toString(), true, name);
		} else if ("oddFooter".equals(name) || "evenFooter".equals(name)
				|| "firstFooter".equals(name)) {
			hfIsOpen = false;
			// output.headerFooter(headerFooter.toString(), false, name);
		}
	}

	private boolean isTime() {
		return formatString != null && formatString.startsWith("[$-F400]");
	}

	/**
	 * Captures characters only if a suitable element is open. Originally was
	 * just "v"; extended for inlineStr also.
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (vIsOpen) {
			value.append(ch, start, length);
		}
		if (fIsOpen) {
			formula.append(ch, start, length);
		}
		if (hfIsOpen) {
			headerFooter.append(ch, start, length);
		}
	}

	private int getRowIndex(String rowStr) {
		rowStr = rowStr.replaceAll("[^A-Z]", "");
		byte[] rowAbc = rowStr.getBytes();
		int len = rowAbc.length;
		float num = 0;
		for (int i = 0; i < len; i++) {
			num += (rowAbc[i] - 'A' + 1) * Math.pow(26, len - i - 1);
		}
		return (int) num;
	}

	public int getTitleRow() {
		return titleRow;
	}

	public void setTitleRow(int titleRow) {
		this.titleRow = titleRow;
	}

	public PackagePart getWorkbookPackagePart() throws XmlException,
            IOException {
		PackageRelationship coreDocRelationship = this.pkg
				.getRelationshipsByType(PackageRelationshipTypes.CORE_DOCUMENT)
				.getRelationship(0);
		// Get the part that holds the workbook
		PackagePart workbookPart = this.pkg.getPart(coreDocRelationship);
		return workbookPart;
	}

	public String getSheetName(int sId) throws InvalidFormatException,
			XmlException, IOException {
		WorkbookDocument doc = WorkbookDocument.Factory
				.parse(getWorkbookPackagePart().getInputStream());
		CTWorkbook workbook = doc.getWorkbook();
		CTSheets sheets = workbook.getSheets();
		CTSheet sheet = sheets.getSheetArray(sId);
		return sheet.getName();
	}

	public int getSheetCount() throws InvalidFormatException, XmlException,
            IOException {
		CTSheets sheets = getSheets();
		return sheets.getSheetList().size();
	}

	public List<String> getSheetNames() throws InvalidFormatException,
			XmlException, IOException {
		CTSheets sheets = getSheets();
		int total = sheets.getSheetList().size();
		List<String> list = new LinkedList<String>();
		for (int i = 0; i < total; i++) {
			CTSheet sheet = sheets.getSheetArray(i);
			list.add(sheet.getName());
		}
		return list;
	}

	private CTSheets getSheets() throws IOException, XmlException {
		WorkbookDocument doc = WorkbookDocument.Factory
				.parse(getWorkbookPackagePart().getInputStream());
		CTWorkbook workbook = doc.getWorkbook();
		CTSheets sheets = workbook.getSheets();
		return  sheets;
	}

}
