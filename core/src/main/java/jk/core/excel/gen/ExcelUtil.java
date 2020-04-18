package jk.core.excel.gen;

import jk.core.ex.ExcelParseException;
import jk.core.excel.cellinfo.CellInfo;
import jk.core.excel.cellinfo.ExcelInputStream;
import jk.core.excel.cellinfo.NavCellInfo;
import jk.core.excel.parse.base.ParserUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import jk.core.ex.ExportException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: ExcelUtil.java
 * @author: Jack lee
 * @version: v1.0
 */
public final class ExcelUtil {

	private static Log log = LogFactory.getLog(ExcelUtil.class);

	private static ThreadLocal<Map> localColors = new ThreadLocal<>();

	/**
	 * <p>
	 * Discription:将提供的数据封装成excel文件，并把excel文件流输出到out流中
	 * </p>
	 * 
	 * @param out
	 * @param SheetConfig
	 * @throws IOException
	 */
	public static void write(OutputStream out, SheetConfig SheetConfig,
							 boolean is2003excel) throws IOException {
		write(out, SheetConfig, is2003excel, false);
	}

	/**
	 * 
	 * @param out
	 *            文件输出流
	 * @param SheetConfig
	 *            excel文件
	 * @param is2003excel
	 *            是否为2003版本excel，true为是，false为不是
	 * @param largeData
	 *            输出文件是否为大数据，true表示是，false表示不是，只有is2003excel为false时生效
	 * @throws IOException
	 */
	public static void write(OutputStream out, SheetConfig SheetConfig,
                             boolean is2003excel, boolean largeData) throws IOException {
		if (isEmpty(out)) {
			throw new ExportException("excel文件输出流为空！");
		}
		List<SheetConfig> sheetConfigs = new ArrayList<SheetConfig>();
		sheetConfigs.add(SheetConfig);
		write(out, sheetConfigs, is2003excel, largeData);
	}

	/**
	 * <p>
	 * Discription:将提供的数据封装成excel文件，并把excel文件写到file文件中
	 * </p>
	 * 
	 * @param file
	 * @param SheetConfig
	 * @throws IOException
	 */
	public static void write(File file, SheetConfig SheetConfig)
			throws IOException {
		write(file, SheetConfig, false);
	}

	/**
	 * 
	 * @param file
	 *            导出的文件句柄
	 * @param SheetConfig
	 *            导出sheet配置文件
	 * @param largeData
	 *            标记是否为大数据导出，只有file参数的文件类型为.xlsx（即excel2007版本以上支持），true表示是，
	 *            false表示一般数据量
	 * @throws IOException
	 */
	public static void write(File file, SheetConfig SheetConfig, boolean largeData) throws IOException {
		if (isEmpty(file)) {
			throw new ExportException("生成excel的文件不能为空");
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		try(FileOutputStream out = new FileOutputStream(file)) {
			boolean is2003excel = file.getName().toLowerCase().endsWith(".xls") ? true : false;
			write(out, SheetConfig, is2003excel, largeData);
			out.flush();
		}
	}

	private static HSSFWorkbook createWorkbook(List<SheetConfig> sheetConfigs, InputStream inputStream)
			throws IOException {
		HSSFWorkbook workbook ;
		if(inputStream != null){
			workbook = new HSSFWorkbook(inputStream);
		}else {
			workbook = new HSSFWorkbook();
		}
		if (!isEmpty(sheetConfigs) && !sheetConfigs.isEmpty()) {
			setHssFRgbColor(workbook, sheetConfigs);
			for (SheetConfig sheetConfig : sheetConfigs) {
				if (sheetConfig == null) {
					continue;
				}

				createSheet(workbook, sheetConfig);
			}
		}
		DocumentSummaryInformation doc = workbook.getDocumentSummaryInformation();//文档摘要信息
		if(doc != null){
			doc.setCompany("公司：--");
			doc.setManager("管理者：--");
		}

		SummaryInformation si = workbook.getSummaryInformation();//摘要信息
		if(si != null) {
			si.setApplicationName("app:excel");
			si.setAuthor("Jack.Lee");
			si.setComments("jk-excel");
			si.setCreateDateTime(new Date());
			si.setTitle("jk-excel");
			si.setComments("--");
		}
		return workbook;
	}


	private static XSSFWorkbook createXssfWorkbook(List<SheetConfig> sheetConfigs, InputStream inputStream)
			throws IOException {
		XSSFWorkbook workbook ;
		if(inputStream != null){
			workbook = new XSSFWorkbook(inputStream);
		}else{
			workbook = new XSSFWorkbook();
		}
		if (!isEmpty(sheetConfigs) && !sheetConfigs.isEmpty()) {
			for (SheetConfig sheetConfig : sheetConfigs) {
				if (sheetConfig == null) {
					continue;
				}
				createSheet(workbook, sheetConfig);
			}
		}
		return workbook;
	}

	/**
	 *
	 * @param sheetConfigs
	 * @return
	 * @throws IOException
	 */
	private static SXSSFWorkbook createSXSSWorkbook(List<SheetConfig> sheetConfigs, InputStream inputStream)
			throws IOException {
		SXSSFWorkbook workbook;
		if(inputStream != null){
			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			workbook = new SXSSFWorkbook(wb);
		}else{
			workbook = new SXSSFWorkbook();
		}
		if (!isEmpty(sheetConfigs) && !sheetConfigs.isEmpty()) {
			for (SheetConfig sheetConfig : sheetConfigs) {
				if (sheetConfig == null) {
					continue;
				}
				createSheet(workbook, sheetConfig);
			}
		}
		return workbook;
	}
	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param workbook
	 * @param sheetConfig
	 * @throws IOException
	 */
	private static void createSheet(Workbook workbook, SheetConfig sheetConfig)
			throws IOException {
		Sheet sheet = workbook.getSheet(sheetConfig.getSheetName());
		if(sheet == null) {
			sheet = workbook.createSheet(sheetConfig.getSheetName());
		}
		Map<Integer, List<ExpHeader>> headers = getHeaders(sheetConfig);
		Integer maxRow = headers.size();
		List<ExpHeader> dataColumns = getDataColumns(maxRow, sheetConfig);
		if(dataColumns == null || dataColumns.size() == 0){
			log.error("not found any column headers,the max row number is "+maxRow+"，please check header configuration arguments,eg:colIndex and rowIndex");
			throw new ExcelParseException("not found any column headers,the max row number is "+maxRow+"，please check header configuration arguments,eg:colIndex and rowIndex");
		}
		createNavInfo(workbook,sheet, sheetConfig);
		createSheetHeader(workbook, sheet, headers, sheetConfig);
		Datasource datasource = sheetConfig.getDatasource();
		List datas = sheetConfig.getDatas();
		if (datasource != null) {
			while (datasource.hasNext()) {
				datas = datasource.loadData(sheetConfig, maxRow);
				if (datas == null || datas.size() == 0) {
					break;
				}
				maxRow = createSheetData(workbook, sheet, datas, dataColumns,
						maxRow, sheetConfig);
			}
		} else {
			maxRow = createSheetData(workbook, sheet, datas, dataColumns,
					maxRow, sheetConfig);
		}
		addPicture(workbook, sheet, sheetConfig, maxRow);
		// sheet.createFreezePane(dataColumns.size() + 10, headers.size());
	}

	private static void createNavInfo(Workbook workbook, Sheet sheet, SheetConfig sheetConfig) {
		NavCellInfo cellInfo = sheetConfig.getNavCellInfo();
		if(cellInfo == null){
			return;
		}
		if(cellInfo.getCol() <= 0 || cellInfo.getRow() <= 0){
			return;
		}
		//提示信息都放在最前面，从0开始
		int rowIndex = 0;
		int colIndex = 0;
		int lastRow = cellInfo.getRow() - 1; //从0开始算
		int lastCol = cellInfo.getCol() - 1; //从0开始算
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
			////设置头部行高
			if( cellInfo.getHeightInPoints() != null && cellInfo.getHeightInPoints() > 0){
				row.setHeightInPoints(cellInfo.getHeightInPoints());
			}
		}


		Cell cell = row.createCell(colIndex);
		CellStyle style = createCellStyle(workbook,cellInfo);
		setString(sheet,cellInfo.getInfo(),cell);
		style.setWrapText(true);
		cell.setCellStyle(style);
		mergeCell(sheet, style,rowIndex,lastRow,colIndex,lastCol);

	}


	private static void setString(Sheet sheet, String val, Cell cell){
		RichTextString richTextString = null;
		if(sheet instanceof HSSFSheet){
			richTextString = new HSSFRichTextString(val);
		}else if(sheet instanceof XSSFSheet){
			richTextString = new XSSFRichTextString(val);
		}else if(sheet instanceof SXSSFSheet){
			richTextString = new XSSFRichTextString(val);
		}else{
			throw new ExcelParseException("not support sheet type!" + sheet);
		}
		cell.setCellValue(richTextString);
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param sheet
	 * @param sheetConfig
	 * @throws IOException
	 */
	private static void addPicture(Workbook workbook, Sheet sheet, SheetConfig sheetConfig, int rowIndex) throws IOException {

		if (sheetConfig.getPictures() == null || sheetConfig.getPictures().size() == 0) {
			return;
		}

		for (Picture picture : sheetConfig.getPictures()) {
			if (picture == null) {
				continue;
			}
			if (picture.getFile() != null) {
				if (!picture.getFile().exists()) {
					throw new ExportException("insert picture file not found：" + picture.getFile().getAbsolutePath());
				}
				FileInputStream is = new FileInputStream( picture.getFile());
				rowIndex = insertPicture(workbook, sheet, is, picture, rowIndex);
			} else if (picture.getInputStream() != null) {
				rowIndex = insertPicture(workbook, sheet,picture.getInputStream(), picture, rowIndex);
			} else if (picture.getChart() != null) {
				ByteArrayOutputStream bout = (ByteArrayOutputStream) picture.getChart().getChartStream();
				ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
				rowIndex = insertPicture(workbook, sheet, bin, picture,rowIndex);
			} else {
				throw new ExportException("not found any picture data!");
			}
		}
	}

	/**
	 *
	 * @param workbook
	 * @param sheet
	 * @param in
	 * @param picture
	 * @param rowIndex
	 * @return
	 * @throws IOException
	 */
	private static int insertPicture(Workbook workbook, Sheet sheet,
									 InputStream in, Picture picture, int rowIndex)
			throws IOException {
		Drawing patriarch = sheet.createDrawingPatriarch();
		BufferedImage image = ImageIO.read(in);
		RowCol rc = getRowCol(image.getWidth(), image.getHeight());
		rowIndex++;
		ClientAnchor anchor = null;
		if (workbook instanceof HSSFWorkbook) {
			anchor = new HSSFClientAnchor(0, 0, 512, 255, (short) 0, rowIndex,
					rc.col, rc.row + rowIndex);
		} else if (workbook instanceof XSSFWorkbook) {
			anchor = new XSSFClientAnchor(0, 0, 512, 255, 0, rowIndex, rc.col,
					rc.row + rowIndex);
		}

		anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, picture.getStringType(), bos);
		int pictureIndex = workbook.addPicture(bos.toByteArray(),picture.getType());
		patriarch.createPicture(anchor, pictureIndex);
		return rowIndex + rc.row + 1;
	}

	private static RowCol getRowCol(int width, int height) {
		int rowHeight = 18;
		int cellWidth = 55;
		RowCol rowCol = new RowCol();
		rowCol.col = (short) (width / cellWidth + 1);
		rowCol.row = height / rowHeight + 1;
		return rowCol;
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param sheet
	 * @param datas
	 * @param dataColumns
	 * @param startIndex
	 */
	private static int createSheetData(Workbook workbook, Sheet sheet,
                                       List datas, List<ExpHeader> dataColumns,
                                       int startIndex, SheetConfig sheetConfig) {
		if (datas == null || datas.isEmpty()) {
			return startIndex;
		}

		checkRowIndex(startIndex + datas.size(),workbook);
		for (Object data : datas) {
			if (data == null) {
				continue;
			}

			Row row = sheet.getRow(startIndex);
			if (row == null) {
				row = sheet.createRow(startIndex);
				//设置数据行高
				if(sheetConfig.getDataRowHeight() > 0){
					row.setHeightInPoints(sheetConfig.getDataRowHeight());
				}
			}
			for (ExpHeader header : dataColumns) {
				if (header == null) {
					continue;
				}
				Cell cell = row.createCell(header.getColIndex());
				createCellStyle(workbook,cell,header);
				setValue(sheet,cell,header,data);
			}
			startIndex++;
		}

		//同步一批数据到硬盘上
		if(workbook instanceof SXSSFWorkbook){
			SXSSFSheet s = (SXSSFSheet)sheet;
			try {
				s.flushRows();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
		return startIndex;
	}

	private static void setValue(Sheet sheet,Cell cell,ExpHeader header,Object data) {
		Object value = getValue(data,header);
		if (value == null) {
			cell.setCellValue("");
			return;
		}
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Float) {
			cell.setCellValue((Float) value);
		} else if ((value instanceof Boolean)
				|| (value.getClass() == boolean.class)) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else if (value instanceof BigDecimal) {
			Double d = ((BigDecimal) value).doubleValue();
			cell.setCellValue(d);
		} else if (value instanceof Date) {
			cell.setCellValue(ParserUtil.formatDate((Date) value));
		} else if (value instanceof java.sql.Date) {
			Date date = new Date();
			java.sql.Date dv = (java.sql.Date) value;
			date.setTime(dv.getTime());
			cell.setCellValue(ParserUtil.formatDate((Date) value));
		} else {
			setString(sheet,value.toString(),cell);
		}
	}

	/**
	 * @param data
	 * @param header
	 * @return
	 */
	private static Object getValue(Object data, ExpHeader header) {
		if(data == null || header == null){
			return null;
		}
		
		if(data instanceof Map)
		{
			Map _map = (Map)data;
			return _map.containsKey(header.getValueName()) ? _map.get(header.getValueName()) : null;
		}
		return getValue(data,header.getValueName());
	}

	/**
	 *
	 * @param data
	 * @param field
	 * @return
	 */
	private static Object getValue(Object data, String field) {
		if(data == null){
			return null;
		}
		try {
			Method method = getMethod(data.getClass(),field);
			if(method == null){
				return null;
			}
			return method.invoke(data);
		} catch (Exception e) {
			// TODO: handle exception
			log.debug(field+"--"+e.getMessage(),e);
		}
		return null;
	}


	/**
	 *
	 * @param cls
	 * @param field
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private static Method getMethod(Class<? extends Object> cls, String field) throws NoSuchMethodException, SecurityException {
		if(cls == Object.class){
			return null;
		}
		String methodName =field.length() >1 ?  "get"+(field.substring(0,1).toUpperCase())+field.substring(1)
				: "get"+(field.substring(0,1).toUpperCase());
		Method m = null;
		try {
			m = cls.getDeclaredMethod(methodName);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		if(m == null){
			return getMethod(cls.getSuperclass(),field);
		}
		return m;
	}

	/**
	 *
	 * @param rowIndex
	 * @param workbook
	 */
	private static void checkRowIndex(int rowIndex, Workbook workbook) {
		int maxIndex = 0;
		if(workbook instanceof SXSSFWorkbook){
			maxIndex = SpreadsheetVersion.EXCEL2007.getLastRowIndex();
		}
		if(workbook instanceof XSSFWorkbook){
			maxIndex = SpreadsheetVersion.EXCEL2007.getLastRowIndex();
		}
		
		if(workbook instanceof HSSFWorkbook){
			maxIndex = SpreadsheetVersion.EXCEL97.getLastRowIndex();
		}
		if(rowIndex > maxIndex){
			throw new ExcelParseException("待导出数据["+rowIndex+"]大于允许导出的最大行数["+maxIndex+"]");
		}
	}

	private static CellStyle createHeadCellStyle(Workbook workbook, ExpHeader header) {
		CellInfo cellInfo = header.getHeadCellInfo();
		return createCellStyle(workbook,cellInfo);
	}

	private static CellStyle createCellStyle(Workbook workbook,CellInfo cellInfo){
		CellStyle style = createCellStyle(workbook);
		Font font = workbook.createFont();
		if(cellInfo != null ){
			setCellStyle(cellInfo,style,font,workbook);
		}else{
			style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			font.setBold(true);
			font.setFontHeightInPoints((short) 12);
			style.setFont(font);
		}
		return style;
	}

	private static void setCellStyle(CellInfo cellInfo,CellStyle style ,Font font,Workbook workbook){
		style.setWrapText(cellInfo.isWrapText());
		if(cellInfo.getFillColor() > 0){
			style.setFillForegroundColor(cellInfo.getFillColor());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}else if(cellInfo.getFillRgbColor() != null){
			setXSSFFillColor(style,cellInfo.getFillRgbColor());
		}
		if(cellInfo.getCharset() > 0){
			font.setCharSet(cellInfo.getCharset());
		}
		if(!isEmpty(cellInfo.getFontName())){
			font.setFontName(cellInfo.getFontName());
		}
		if(cellInfo.getColor() > 0){
			font.setColor(cellInfo.getColor());
		}else if(cellInfo.getRgbColor() != null){
			setXSSFFontColor(font,cellInfo.getRgbColor());
		}
		if(cellInfo.getFontHightInPoints() > 0){
			font.setFontHeightInPoints(cellInfo.getFontHightInPoints());
		}
		if(cellInfo.isBoldWeight()){
			font.setBold(true);
		}else{
			font.setBold(false);
		}
		style.setFont(font);
		short dataFormatIndex = 0;
		if(cellInfo.getDataFormat() != null && cellInfo.getDataFormat().trim().length() > 0){
			DataFormat dataFormat = workbook.createDataFormat();
			dataFormatIndex = dataFormat.getFormat(cellInfo.getDataFormat());
//			dataFormatIndex = (short)BuiltinFormats.getBuiltinFormat(cellInfo.getDataFormat());
			if(dataFormatIndex == -1){
				dataFormatIndex = 0;
				log.warn("the dataFormat is not found : " + cellInfo.getDataFormat());
			}
		}else if(cellInfo.getDataFormatIndex() != null){
			dataFormatIndex = cellInfo.getDataFormatIndex();
		}

		style.setDataFormat(dataFormatIndex);
	}

	/**
	 * 设置xssf 字体颜色
	 * @param font
	 * @param rgb
	 */
	private static void setXSSFFontColor(Font font, byte[] rgb) {
		if(font instanceof XSSFFont){
			XSSFFont xssfFont = (XSSFFont)font;
			XSSFColor xc = getXssfColor(rgb);
			xssfFont.setColor(xc);
		}
	}

	/**
	 * 设置xssf背景颜色
	 * @param style
	 * @param rgb
	 */
	private static void setXSSFFillColor(CellStyle style,byte[] rgb){
		if(style instanceof  XSSFCellStyle){
			XSSFColor xc = getXssfColor(rgb);
			XSSFCellStyle xssfCellStyle = (XSSFCellStyle)style;
			xssfCellStyle.setFillForegroundColor(xc);
			xssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
	}

	/**
	 *
	 * @param workbook
	 * @param cell
	 * @param header
	 */
	private static void createCellStyle(Workbook workbook, Cell cell,
			ExpHeader header) {
		CellStyle defaultStyle = null;
		if(header.getCellStyle() != null){
			cell.setCellStyle(header.getCellStyle());
			defaultStyle = header.getCellStyle();
		}

		if(header.isGenStyle()){
			return ;
		}
		CellInfo cellInfo = header.getCellInfo();
		Font font = workbook.createFont();
		if(cellInfo != null ){
			CellStyle style = createCellStyle(workbook);
			setCellStyle(cellInfo,style,font,workbook);
			header.setCellStyle(style);
			defaultStyle = style;
		}
		cell.setCellStyle(defaultStyle);
		header.setGenStyle(true);
	}

	/**
	 * <p>
	 * Discription:根据列描述创建列头
	 * </p>
	 * 
	 * @param sheet
	 * @param headers
	 */
	private static void createSheetHeader(Workbook workbook, Sheet sheet,
										  Map<Integer, List<ExpHeader>> headers, SheetConfig sheetConfig) {
		for (Iterator<Integer> keys = headers.keySet().iterator(); keys.hasNext();) {
			Integer rowIndex = keys.next();
			List<ExpHeader> cols = headers.get(rowIndex);
			if(cols == null || cols.size() == 0){
				continue;
			}
			Row row = sheet.getRow(rowIndex);
			if (row == null) {
				row = sheet.createRow(rowIndex);
				////设置头部行高
				if(sheetConfig.getHeadRowHeight() > 0){
					row.setHeightInPoints(sheetConfig.getHeadRowHeight());
				}
			}

			for (ExpHeader col : cols) {
				if (col != null) {
					if(col.isAutoWidth()) {
						sheet.setColumnWidth(col.getColIndex(), (col.getName().getBytes().length + 2) * 256);
					}else if(col.getColWidth() > 0){
						sheet.setColumnWidth(col.getColIndex(), (col.getColWidth() + 2) * 256);
					}
					Cell cell = row.createCell(col.getColIndex());
					CellStyle style = createHeadCellStyle(workbook,col);
					cell.setCellValue(col.getName());
					cell.setCellStyle(style);
					mergeCell(sheet, col, style);
					addConstraints(col,sheet);
				}
			}
		}

	}

	private static void addConstraints(ExpHeader col, Sheet sheet) {
		if(col.getConstraintData() == null || col.getConstraintData().size() == 0){
			return;
		}
		DataValidation dataValidation = null ;
		if(sheet instanceof HSSFSheet){
			dataValidation = createHssfValidation(col);
		}else if(sheet instanceof XSSFSheet){
			dataValidation = createXssfValidation(col,sheet);
		}else if(sheet instanceof SXSSFSheet){
			dataValidation = createXssfValidation(col,sheet);
		}else{
			throw new ExcelParseException("没有可支持的sheet类型");
		}

		sheet.addValidationData(dataValidation);
	}

	private static DataValidation appendValidation(DataValidation validation) {
		validation.createPromptBox("操作提示", "请选择下拉选中的值");
		validation.createErrorBox("错误提示", "请从下拉选中选择，不要随便输入");
		return validation;
	}

	private static DataValidation createHssfValidation(ExpHeader col) {
		String[] textlist = col.getConstraintData().toArray(new String[col.getConstraintData().size()]);
		// 加载下拉列表内容
		DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(col.getRowIndex()+1,9999, col.getColIndex(), col.getColIndex());
		// 数据有效性对象
		HSSFDataValidation validation = new HSSFDataValidation(regions, constraint);
		validation.setSuppressDropDownArrow(false);
		validation.setShowErrorBox(true);
		return appendValidation(validation);
	}

	private static DataValidation createXssfValidation(ExpHeader col, Sheet sheet) {
		String[] textlist = col.getConstraintData().toArray(new String[col.getConstraintData().size()]);
		XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(textlist);
		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(col.getRowIndex()+1,9999, col.getColIndex(), col.getColIndex());

		XSSFDataValidationHelper helper = (XSSFDataValidationHelper)sheet.getDataValidationHelper();
		DataValidation validation = helper.createValidation(constraint,regions);
		validation.setSuppressDropDownArrow(true);
		validation.setShowErrorBox(true);
		return appendValidation(validation);
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param sheet
	 * @param col
	 */
	private static void mergeCell(Sheet sheet, ExpHeader col, CellStyle style) {
		if (col.getCol() == 1 && col.getRow() == 1) {
			return;
		}
		int lastRow = col.getRowIndex() + col.getRow() - 1;
		int lastCol = col.getColIndex() + col.getCol() - 1;
		// int firstRow, int lastRow, int firstCol, int lastCol
		mergeCell(sheet,  style,col.getRowIndex(),lastRow,col.getColIndex(),lastCol);
	}

	private static void mergeCell(Sheet sheet,CellStyle style,int rowIndex,int lastRow,int colIndex,int lastCol){
		CellRangeAddress range = new CellRangeAddress(rowIndex,lastRow, colIndex, lastCol);
		sheet.addMergedRegion(range);
		for (int row = rowIndex; row <= lastRow; row++) {
			Row hssfRow = sheet.getRow(row);
			if (hssfRow == null) {
				hssfRow = sheet.createRow(row);
			}
			for (int c = colIndex; c <= lastCol; c++) {
				Cell cell = hssfRow.getCell(c);
				if (cell == null) {
					cell = hssfRow.createCell(c);
				}
				cell.setCellStyle(style);
			}
		}
	}

	private static CellStyle createCellStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}

	/**
	 * 计算出取数据的列
	 * @param rowIndex
	 * @param sheetConfig
	 * @return
	 */
	private static List<ExpHeader> getDataColumns(int rowIndex,
												  SheetConfig sheetConfig) {
		List<ExpHeader> allHeaders = sheetConfig.getHeaders();
		List<ExpHeader> hList = new ArrayList<ExpHeader>();
		for (ExpHeader header : allHeaders) {
			if (header != null) {
				if (header.getRow() + header.getRowIndex() == rowIndex) {
					hList.add(header);
				}
			}
		}
		// 按照列对头部数据进行排序；
		Collections.sort(hList, new Comparator<ExpHeader>() {
			public int compare(ExpHeader o1, ExpHeader o2) {
				return o1.getCol() - o2.getCol();
			}
		});
		return hList;
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param sheetConfig
	 * @return
	 */
	private static Map<Integer, List<ExpHeader>> getHeaders(SheetConfig sheetConfig) {
		List<ExpHeader> allHeaders = sheetConfig.getHeaders();
		Map<Integer, List<ExpHeader>> headers = new HashMap<Integer, List<ExpHeader>>();
		// 按照列对头部数据进行排序；
		Collections.sort(allHeaders, new Comparator<ExpHeader>() {
			public int compare(ExpHeader o1, ExpHeader o2) {
				return o1.getCol() - o2.getCol();
			}
		});
		int maxRowIndex = 0;
		for (ExpHeader header : allHeaders) {
			if (header != null) {
				if(header.getRowIndex() > maxRowIndex){
					maxRowIndex = header.getRowIndex();
				}
				if (headers.containsKey(header.getRowIndex())) {
					headers.get(header.getRowIndex()).add(header);
				} else {
					List<ExpHeader> hList = new ArrayList<ExpHeader>();
					hList.add(header);
					headers.put(header.getRowIndex(), hList);
				}
			}
		}

		for(int i = 0 ; i < maxRowIndex ; i++){
			if(headers.containsKey(i)){
				continue;
			}
			headers.put(i,null);
		}

		return headers;
	}


	static class RowCol {
		public short col;

		public int row;
	}

	public static boolean isEmpty(Object obj) {
		return Objects.isNull(obj) || "".equals(obj);
	}

	private static XSSFColor getXssfColor(byte[] rgbColor){

		if(rgbColor == null || rgbColor.length != 3){
			return null;
		}
		Map<String, Color> _cs = localColors.get();

//				java.awt.Color color = new java.awt.Color(234,111,143);
//				XSSFColor c = new XSSFColor(new byte[]{(byte)234,(byte)222,(byte)213});

		String hex = byteToHex(rgbColor);

		if(_cs == null){
			_cs = new HashMap<>();
			localColors.set(_cs);
		}
		if(!_cs.containsKey(hex)){
			XSSFColor c = new XSSFColor();
			c.setRGB(rgbColor);
			_cs.put(hex,c);
		}

		return (XSSFColor)_cs.get(hex);
	}

	private static String byteToHex(byte[] b){
		StringBuffer buffer = new StringBuffer();
		for(int i = 0 ; i < b.length ; i++){
			int ib = b[i];
			ib = ib & 0xff;
			buffer.append(Integer.toHexString(ib));
		}
		return buffer.toString();
	}
	private static void setHssFRgbColor(HSSFWorkbook workbook, List<SheetConfig> sheetConfigs) {

		List<Short> existIndexs = Collections.synchronizedList(new ArrayList());
		Map<String,byte[]> changeColors = new ConcurrentHashMap<>();
		for(SheetConfig sheetConfig : sheetConfigs){
			if(sheetConfig == null){
				continue;
			}
			List<ExpHeader> headers = sheetConfig.getHeaders();
			for(ExpHeader header : headers){
				if(header == null){
					continue;
				}
				if(header.getCellInfo() != null){
					checkColors(existIndexs,changeColors,header.getCellInfo());
				}
				if(header.getHeadCellInfo() != null){
					checkColors(existIndexs,changeColors,header.getHeadCellInfo());
				}
			}
		}
		Map<String, Integer> colorIndexMapping = new ConcurrentHashMap<>();
		changeHSSFColor(workbook,existIndexs,changeColors,colorIndexMapping);
		changeColorIndex(sheetConfigs,colorIndexMapping);
	}

	/**
	 * 如果color或者fillColor未设置，而rgbColor设置，则更新对应color或fillColor
	 * @param sheetConfigs
	 * @param colorIndexMapping
	 */
	private static void changeColorIndex(List<SheetConfig> sheetConfigs, Map<String, Integer> colorIndexMapping) {
		if(colorIndexMapping.size() > 0){
			for(SheetConfig sheetConfig : sheetConfigs){
				List<ExpHeader> headers = sheetConfig.getHeaders();

				for(ExpHeader header : headers){
					if(header.getCellInfo() != null){
						_changeColorIndex(header.getCellInfo(),colorIndexMapping);
					}

					if(header.getHeadCellInfo() != null){
						_changeColorIndex(header.getHeadCellInfo(),colorIndexMapping);
					}
				}
			}
		}
	}

	private static void _changeColorIndex(CellInfo info, Map<String, Integer> colorIndexMapping){
		String hex = "";
		if(info.getColor() < 0 && info.getRgbColor() != null ){
			hex = byteToHex(info.getRgbColor());
			if(colorIndexMapping.containsKey(hex)){
				info.setColor((short)(colorIndexMapping.get(hex).intValue()));
			}
		}

		if(info.getFillColor() < 0 && info.getFillRgbColor() != null){
			hex = byteToHex(info.getFillRgbColor());
			if(colorIndexMapping.containsKey(hex)){
				info.setFillColor((short)(colorIndexMapping.get(hex).intValue()));
			}
		}
	}

	/**
	 * 生成指定的颜色到workbook中
	 * @param workbook
	 * @param existIndexs
	 * @param changeColors
	 * @param colorIndexMapping
	 */
	private static void changeHSSFColor(HSSFWorkbook workbook, List<Short> existIndexs
			, Map<String,byte[]> changeColors, Map<String, Integer> colorIndexMapping ){
		if(changeColors.size() > 0){
			Iterator<Map.Entry<String,byte[]>> colorEntry = changeColors.entrySet().iterator();
			//workbook自定义的颜色只能是从8~64 index的颜色，作为内置颜色，如果需要修改，针对这些颜色进行修改，替换为自己的颜色
			//如果cellInfos颜色数量超过，将会被抛弃，不会做任何提示
			HSSFPalette palette = workbook.getCustomPalette();
			for(int i = 8 ; i < 64 ; i++){
				if(!existIndexs.contains(i)){
					if(!colorEntry.hasNext()){
						return;
					}
					Map.Entry<String,byte[]> entry = colorEntry.next();
					byte[] rgb = entry.getValue();
					palette.setColorAtIndex((short)i,rgb[0],rgb[1],rgb[2]);
					colorIndexMapping.put(entry.getKey(),i);
				}
			}
			if(changeColors.size() > 0){
				log.warn("还有【"+changeColors.size()+"】类颜色未处理，它们的设置将不会生效");
			}
		}

	}

	private static void checkColors(List<Short> existIndexs, Map<String,byte[]> changeColors, CellInfo cellInfo) {
		if( cellInfo.getFillColor() > 0){//背景色
			//记录高优先级的color index
			existIndexs.add(cellInfo.getFillColor());
		}
		if(cellInfo.getColor() > 0){//字体颜色
			existIndexs.add(cellInfo.getColor());
		}
		if(cellInfo.getRgbColor() != null && cellInfo.getRgbColor().length == 3){//rgb字体颜色
			//合并重复颜色
			changeColors.put(byteToHex(cellInfo.getRgbColor()),cellInfo.getRgbColor());
		}
		if(cellInfo.getFillRgbColor() != null && cellInfo.getFillRgbColor().length == 3){//rgb背景色
			//合并重复颜色
			changeColors.put(byteToHex(cellInfo.getFillRgbColor()),cellInfo.getFillRgbColor());
		}
	}


	/**
	 * 将提供的数据封装成excel文件，并把excel文件写到fileName文件中
	 * @param fileName
	 * @param SheetConfig
	 * @throws IOException
	 */
	public static void write(String fileName, SheetConfig SheetConfig)
			throws IOException {
		write(fileName, SheetConfig, false);
	}

	/**
	 *
	 * @param fileName
	 * @param SheetConfig
	 * @param largeData
	 * @throws IOException
	 */
	public static void write(String fileName, SheetConfig SheetConfig,
                             boolean largeData) throws IOException {
		if (isEmpty(fileName)) {
			throw new ExportException("生成excel的文件不能为空");
		}
		File file = new File(fileName);
		write(file, SheetConfig, largeData);
	}

	/**
	 * 将提供的数据封装成excel文件，并把excel文件流输出到out流中
	 * @param out
	 * @param sheetConfigs
	 * @param is2003excel
	 * @throws IOException
	 */
	public static void write(OutputStream out, List<SheetConfig> sheetConfigs,
                             boolean is2003excel) throws IOException {
		write(out, sheetConfigs, is2003excel, false);
	}

	/**
	 *
	 * @param out
	 *            文件输出流
	 * @param sheetConfigs
	 *            每隔sheet的配置说明
	 * @param is2003excel
	 *            导出的文件流是否为2003版本，true是，false不是
	 * @param largeData
	 *            导出的数据是否为大量数据，true是，false不是；为true时，仅is2003excel为true才能生效
	 * @throws IOException
	 */
	public static void write(OutputStream out, List<SheetConfig> sheetConfigs,
                             boolean is2003excel, boolean largeData) throws IOException {
		if (isEmpty(out)) {
			throw new ExportException("excel文件输出流为空！");
		}
		Workbook workbook = null;
		if (is2003excel) {
			workbook = createWorkbook(sheetConfigs,null);
		} else {
			if (largeData) {
				workbook = createSXSSWorkbook(sheetConfigs,null);
			} else {
				workbook = createXssfWorkbook(sheetConfigs,null);
			}
		}
		workbook.write(out);
		try {
			workbook.close();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}



	/**
	 * 将提供的数据封装成excel文件，并把excel文件写到file文件中
	 * @param file
	 * @param sheetConfigs
	 * @throws IOException
	 */
	public static void write(File file, List<SheetConfig> sheetConfigs)
			throws IOException {
		write(file, sheetConfigs, false);
	}

	/**
	 * 设置导出大量数据
	 *
	 * @param file
	 *            导出的文件
	 * @param sheetConfigs
	 *            sheet配置文件
	 * @param largeData
	 *            是否为数据，true以大数据的形式导出，false不是，file类型必须为2007版以上的才生效
	 * @throws IOException
	 */
	public static void write(File file, List<SheetConfig> sheetConfigs,
                             boolean largeData) throws IOException {
		if (isEmpty(file)) {
			throw new ExportException("生成excel的文件不能为空");
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);
		boolean is2003excel = file.getName().toLowerCase().endsWith(".xls") ? true: false;
		write(out, sheetConfigs, is2003excel, largeData);
		out.flush();
		out.close();
	}

	/**
	 * 将提供的数据封装成excel文件，并把excel文件写到fileName文件中
	 * @param fileName
	 * @param sheetConfigs
	 * @throws IOException
	 */
	public static void write(String fileName, List<SheetConfig> sheetConfigs)
			throws IOException {
		write(fileName, sheetConfigs, false);
	}

	/**
	 *
	 * @param fileName
	 * @param sheetConfigs
	 * @param largeData
	 *            导出操作为大数据标示，true为大数据，false不是
	 * @throws IOException
	 */
	public static void write(String fileName, List<SheetConfig> sheetConfigs,
                             boolean largeData) throws IOException {
		if (isEmpty(fileName)) {
			throw new ExportException("生成excel的文件不能为空");
		}
		File file = new File(fileName);
		write(file, sheetConfigs, largeData);
	}

	/**
	 * 将提供的数据封装成excel文件，并把excel文件流输出到out流中
	 * @param out
	 *            文件输出流
	 * @param sheetConfigs
	 *            每隔sheet的配置说明
	 * @param is2003excel
	 *            导出的文件流是否为2003版本，true是，false不是
	 * @param excelInputStream
	 * 				存在excel基础上新增sheet模板
	 * @throws IOException
	 */
	public static void write(OutputStream out, List<SheetConfig> sheetConfigs,
                             boolean is2003excel, ExcelInputStream excelInputStream) throws IOException {
		write(out, sheetConfigs, is2003excel, false,excelInputStream);
	}

	/**
	 *
	 * @param out
	 *            文件输出流
	 * @param sheetConfigs
	 *            每隔sheet的配置说明
	 * @param is2003excel
	 *            导出的文件流是否为2003版本，true是，false不是
	 * @param largeData
	 *            导出的数据是否为大量数据，true是，false不是；为true时，仅is2003excel为true才能生效
	 * @param excelInputStream
	 * 				存在excel基础上新增sheet模板
	 * @throws IOException
	 */
	public static void write(OutputStream out, List<SheetConfig> sheetConfigs,
                             boolean is2003excel, boolean largeData, ExcelInputStream excelInputStream) throws IOException {
		if (isEmpty(out)) {
			throw new ExportException("excel文件输出流为空！");
		}
		InputStream inputStream = getInputStream(excelInputStream);
		Workbook workbook = null;
		if (is2003excel) {
			workbook = createWorkbook(sheetConfigs,inputStream);
		} else {
			if (largeData) {
				workbook = createSXSSWorkbook(sheetConfigs,inputStream);
			} else {
				workbook = createXssfWorkbook(sheetConfigs,inputStream);
			}
		}
		workbook.write(out);
		try {
			workbook.close();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * 将提供的数据封装成excel文件，并把excel文件写到file文件中
	 * @param file
	 * @param sheetConfigs
	 * @throws IOException
	 */
	public static void write(File file, List<SheetConfig> sheetConfigs, ExcelInputStream excelInputStream)
			throws IOException {
		write(file, sheetConfigs, false,excelInputStream);
	}

	/**
	 * 设置导出大量数据
	 *
	 * @param file
	 *            导出的文件
	 * @param sheetConfigs
	 *            sheet配置文件
	 * @param largeData
	 *            是否为数据，true以大数据的形式导出，false不是，file类型必须为2007版以上的才生效
	 * @throws IOException
	 */
	public static void write(File file, List<SheetConfig> sheetConfigs,
                             boolean largeData, ExcelInputStream excelInputStream) throws IOException {
		if (isEmpty(file)) {
			throw new ExportException("生成excel的文件不能为空");
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file);
		boolean is2003excel = file.getName().toLowerCase().endsWith(".xls") ? true: false;
		write(out, sheetConfigs, is2003excel, largeData,excelInputStream);
		out.flush();
		out.close();
	}

	private static InputStream getInputStream(ExcelInputStream excelInputStream) throws IOException {
		if(excelInputStream == null){
			return  null;
		}
		if(excelInputStream.getInputStream() != null){
			return excelInputStream.getInputStream();
		}
		if(excelInputStream.getFile() != null){
			return new FileInputStream(excelInputStream.getFile());
		}
		if(excelInputStream.getUrl() != null){
			return excelInputStream.getUrl().openStream();
		}
		return null;
	}

}
