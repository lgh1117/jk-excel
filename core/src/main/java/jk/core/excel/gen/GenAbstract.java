package jk.core.excel.gen;

import jk.core.GenExcel;
import jk.core.ex.ExcelParseException;
import jk.core.ex.ExportException;
import jk.core.excel.cellinfo.CellInfo;
import jk.core.excel.cellinfo.ExcelInputStream;
import jk.core.excel.cellinfo.NavCellInfo;
import jk.core.excel.parse.base.ParserUtil;
import jk.core.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName GenAbstract
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 */
public abstract class GenAbstract implements GenExcel {

    private static final Logger logger = LogManager.getLogger(GenAbstract.class);

    private GenConfig config;

    public GenAbstract(GenConfig config){
        this.config = config;
    }

    @Override
    public boolean write() throws ExportException {
        long time = System.currentTimeMillis();
        if(config.getOut() == null){
            throw new ExportException("output stream is null ");
        }
        if(Utils.isEmptyCollection(config.getSheetConfigs())){
            throw new ExportException("not found any sheet config info ");
        }
        try {
            boolean rs = write(config.getOut(), config.getSheetConfigs(), config.getExcelInputStream());
            return rs;
        }finally {
            logger.info("create excel file used " + (System.currentTimeMillis() - time) + " ms");
            close();
        }
    }

    public abstract boolean write(OutputStream out, List<SheetConfig> configs, ExcelInputStream excelInputStream) throws ExportException;


    public void checkColors(List<Short> existIndexs, Map<String,byte[]> changeColors, CellInfo cellInfo) {
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

    public String byteToHex(byte[] b){
        if(Utils.isEmpty(b)){
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for(int i = 0 ; i < b.length ; i++){
            int ib = b[i];
            ib = ib & 0xff;
            buffer.append(Integer.toHexString(ib));
        }
        return buffer.toString();
    }

    public InputStream getInputStream(ExcelInputStream excelInputStream) throws IOException {
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

    /**
     * <p>
     * Discription:[方法功能中文描述]
     * </p>
     *
     * @param workbook
     * @param sheetConfig
     * @throws IOException
     */
    public void createSheet(Workbook workbook, SheetConfig sheetConfig) throws IOException {
        Sheet sheet = workbook.getSheet(sheetConfig.getSheetName());
        if(sheet == null) {
            sheet = workbook.createSheet(sheetConfig.getSheetName());
        }
        Map<Integer, List<ExpHeader>> headers = getHeaders(sheetConfig);
        Integer maxRow = headers.size();
        List<ExpHeader> dataColumns = getDataColumns(maxRow, sheetConfig);
        if(dataColumns == null || dataColumns.size() == 0){
            logger.error("not found any column headers,the max row number is "+maxRow+"，please check header configuration arguments,eg:colIndex and rowIndex");
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
                maxRow = createSheetData(workbook, sheet, datas, dataColumns, maxRow, sheetConfig);
            }
        } else {
            maxRow = createSheetData(workbook, sheet, datas, dataColumns,maxRow, sheetConfig);
        }
        addPicture(workbook, sheet, sheetConfig, maxRow);
        // sheet.createFreezePane(dataColumns.size() + 10, headers.size());
    }

    /**
     * <p>
     * Discription:[方法功能中文描述]
     * </p>
     *
     * @param sheetConfig
     * @return
     */
    private Map<Integer, List<ExpHeader>> getHeaders(SheetConfig sheetConfig) {
        List<ExpHeader> allHeaders = sheetConfig.getHeaders();
        if(Utils.isEmptyCollection(allHeaders)){
            throw new ExportException("export excel headers is null or empty");
        }
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

    /**
     * 计算出取数据的列
     * @param rowIndex
     * @param sheetConfig
     * @return
     */
    private List<ExpHeader> getDataColumns(int rowIndex,SheetConfig sheetConfig) {
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

    private void createNavInfo(Workbook workbook, Sheet sheet, SheetConfig sheetConfig) {
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
        setString(cellInfo.getInfo(),cell);
        style.setWrapText(true);
        cell.setCellStyle(style);
        mergeCell(sheet, style,rowIndex,lastRow,colIndex,lastCol);

    }

    /**
     * <p>
     * Discription:根据列描述创建列头
     * </p>
     *
     * @param sheet
     * @param headers
     */
    private void createSheetHeader(Workbook workbook, Sheet sheet,Map<Integer, List<ExpHeader>> headers
                                        , SheetConfig sheetConfig) {
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
    private int createSheetData(Workbook workbook, Sheet sheet,List datas, List<ExpHeader> dataColumns
                                ,int startIndex, SheetConfig sheetConfig) {
        if (datas == null || datas.isEmpty()) {
            return startIndex;
        }
        checkRowIndex(startIndex + datas.size());
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
                setValue(cell,header,data);
            }
            startIndex++;
        }

        flushRows(sheet);
        return startIndex;
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
    private void addPicture(Workbook workbook, Sheet sheet, SheetConfig sheetConfig, int rowIndex) throws IOException {
        if (sheetConfig.getPictures() == null || sheetConfig.getPictures().size() == 0) {
            return;
        }

        for (Picture picture : sheetConfig.getPictures()) {
            if (picture == null) {
                continue;
            }
            rowIndex = insertPicture(workbook, sheet, getPicture(picture), picture,rowIndex);
        }
    }

    private InputStream getPicture(Picture picture) throws IOException {
        if (picture.getFile() != null) {
            if (!picture.getFile().exists()) {
                throw new ExportException("insert picture file not found：" + picture.getFile().getAbsolutePath());
            }
            return new FileInputStream( picture.getFile());
        } else if (picture.getInputStream() != null) {
            return picture.getInputStream();
        } else if (picture.getChart() != null) {
            ByteArrayOutputStream bout = (ByteArrayOutputStream) picture.getChart().getChartStream();
            return new ByteArrayInputStream(bout.toByteArray());
        } else {
            throw new ExportException("not found any picture data!");
        }
    }

    /**
     *
     * @param workbook
     * @param cell
     * @param header
     */
    private void createCellStyle(Workbook workbook, Cell cell,ExpHeader header) {
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


    private CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private  CellStyle createCellStyle(Workbook workbook,CellInfo cellInfo){
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

    private void setCellStyle(CellInfo cellInfo,CellStyle style ,Font font,Workbook workbook){
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
        if(!Utils.isEmpty(cellInfo.getFontName())){
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
            if(dataFormatIndex == -1){
                dataFormatIndex = 0;
                logger.warn("the dataFormat is not found : " + cellInfo.getDataFormat());
            }
        }else if(cellInfo.getDataFormatIndex() != null){
            dataFormatIndex = cellInfo.getDataFormatIndex();
        }

        style.setDataFormat(dataFormatIndex);
    }



    /**
     * <p>
     * Discription:[方法功能中文描述]
     * </p>
     *
     * @param sheet
     * @param col
     */
    private void mergeCell(Sheet sheet, ExpHeader col, CellStyle style) {
        if (col.getCol() == 1 && col.getRow() == 1) {
            return;
        }
        int lastRow = col.getRowIndex() + col.getRow() - 1;
        int lastCol = col.getColIndex() + col.getCol() - 1;
        // int firstRow, int lastRow, int firstCol, int lastCol
        mergeCell(sheet,  style,col.getRowIndex(),lastRow,col.getColIndex(),lastCol);
    }

    private void mergeCell(Sheet sheet,CellStyle style,int rowIndex,int lastRow,int colIndex,int lastCol){
        CellRangeAddress range = new CellRangeAddress(rowIndex,lastRow, colIndex, lastCol);
        sheet.addMergedRegion(range);
        for (int row = rowIndex; row <= lastRow; row++) {
            Row sheetRow = sheet.getRow(row);
            if (sheetRow == null) {
                sheetRow = sheet.createRow(row);
            }
            for (int c = colIndex; c <= lastCol; c++) {
                Cell cell = sheetRow.getCell(c);
                if (cell == null) {
                    cell = sheetRow.createCell(c);
                }
                cell.setCellStyle(style);
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
    private int insertPicture(Workbook workbook, Sheet sheet,InputStream in, Picture picture, int rowIndex)
            throws IOException {
        try {
            Drawing patriarch = sheet.createDrawingPatriarch();
            BufferedImage image = ImageIO.read(in);
            RowCol rc = getRowCol(image.getWidth(), image.getHeight());
            rowIndex++;
            ClientAnchor anchor = createClientAnctor(rowIndex,rc.col,rc.row + rowIndex);
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, picture.getStringType(), bos);
            int pictureIndex = workbook.addPicture(bos.toByteArray(), picture.getType());
            patriarch.createPicture(anchor, pictureIndex);
            return rowIndex + rc.row + 1;
        }finally {
            if(in != null){
                in.close();
            }
        }
    }

    private RowCol getRowCol(int width, int height) {
        int rowHeight = 18;
        int cellWidth = 55;
        RowCol rowCol = new RowCol();
        rowCol.col = (short) (width / cellWidth + 1);
        rowCol.row = height / rowHeight + 1;
        return rowCol;
    }

    private CellStyle createHeadCellStyle(Workbook workbook, ExpHeader header) {
        CellInfo cellInfo = header.getHeadCellInfo();
        return createCellStyle(workbook,cellInfo);
    }

    private void addConstraints(ExpHeader col, Sheet sheet) {
        if(col.getConstraintData() == null || col.getConstraintData().size() == 0){
            return;
        }
        DataValidation dataValidation = createValidation(col,sheet) ;
        sheet.addValidationData(dataValidation);
    }

    private void setValue(Cell cell,ExpHeader header,Object data) {
        Object value = getValue(data,header);
        if (value == null) {
            cell.setCellValue("");
            return;
        }
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Float) {
            cell.setCellValue((Float) value);
        } else if ((value instanceof Boolean) || (value.getClass() == boolean.class)) {
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
            setString(value.toString(),cell);
        }
    }

    private Object getValue(Object data, ExpHeader header) {
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

    private Object getValue(Object data, String field) {
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
            logger.debug(field+"--"+e.getMessage(),e);
        }
        return null;
    }

    private Method getMethod(Class<? extends Object> cls, String field) throws NoSuchMethodException, SecurityException {
        if(cls == Object.class){
            return null;
        }
        String methodName = field.length() > 1 ?  (field.substring(0,1).toUpperCase()) + field.substring(1)
                : (field.substring(0,1).toUpperCase());
        Method m = null;
        try {
            m = cls.getDeclaredMethod("get" + methodName);
        } catch (Exception e) {
            logger.debug(e.getMessage(),e);
            try {
                m = cls.getDeclaredMethod("is" + methodName);
            } catch (Exception ex) {
                logger.debug(ex.getMessage(),ex);
            }
        }
        if(m == null){
            return getMethod(cls.getSuperclass(),field);
        }
        return m;
    }

    public DataValidation appendValidation(DataValidation validation) {
        validation.createPromptBox("操作提示", "请选择下拉选中的值");
        validation.createErrorBox("错误提示", "请从下拉选中选择，不要随便输入");
        return validation;
    }

    /**
     * 获取数据头部校验规则
     * @param col
     * @param sheet
     * @return
     */
    public abstract DataValidation createValidation(ExpHeader col, Sheet sheet) ;

    /**
     * 验证excel单个sheet允许的最大行数
     * @param rowIndex
     */
    public abstract  void checkRowIndex(int rowIndex) ;

    public void setXSSFFillColor(CellStyle style, byte[] fillRgbColor){
        throw new ExportException("setXSSFFillColor not support in your generator");
    }

    public void setXSSFFontColor(Font font, byte[] rgbColor) {
        throw new ExportException("setXSSFFontColor not support in your generator");
    }

    public abstract ClientAnchor createClientAnctor(int rowIndex, short col, int row) ;

    public abstract void setString(String val, Cell cell);

    /**
     * 同步一批数据到硬盘上
     * only sxssf type excel
     * @param sheet
     */
    public void flushRows(Sheet sheet) {
        throw new ExportException("flushRows not support in your generator");
    }

    class RowCol {
        public short col;
        public int row;
    }

}


