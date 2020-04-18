package jk.core.excel.gen;

import jk.core.ex.ExcelParseException;
import jk.core.ex.ExportException;
import jk.core.excel.cellinfo.ExcelInputStream;
import jk.core.util.Utils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName XsslGenerator
 * @Description
 * @Version 1.0.0
 * @Author Jack lee
 * @Since 2020/4/18 下午10:25
 */
public class XsslGenerator extends GenAbstract {

    private static final Logger logger = Logger.getLogger(XsslGenerator.class);

    private GenConfig config ;
    private XSSFWorkbook workbook = null;
    private InputStream inputStream ;
    private ThreadLocal<Map> localColors = new ThreadLocal<>();

    public XsslGenerator(GenConfig config){
        super(config);
        this.config = config;
    }

    @Override
    public boolean write(OutputStream out, List<SheetConfig> configs, ExcelInputStream excelInputStream) throws ExportException {
        try {
            inputStream = getInputStream(excelInputStream);
            if (inputStream != null) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook();
            }
            if (!Utils.isEmptyCollection(configs)) {
                for (SheetConfig sheetConfig : configs) {
                    if (sheetConfig == null) {
                        continue;
                    }
                    createSheet(workbook, sheetConfig);
                }
            }
            return true;
        }catch (Exception ex){
            throw new ExportException(ex.getMessage(),ex);
        }finally {
            close();
        }
    }

    @Override
    public void close() throws ExportException {
        try {
            workbook.close();
        } catch (Exception e) {
            throw new ExportException(e.getMessage(), e);
        }
        if(inputStream != null ){
            try {
                inputStream.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        if(config.getOut() != null){
            try {
                config.getOut().close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

    @Override
    public DataValidation createValidation(ExpHeader col, Sheet sheet) {
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

    @Override
    public void checkRowIndex(int rowIndex) {
        int maxIndex = SpreadsheetVersion.EXCEL2007.getLastRowIndex();
        if(rowIndex > maxIndex){
            throw new ExcelParseException("待导出数据["+rowIndex+"]大于允许导出的最大行数["+maxIndex+"]");
        }
    }

    @Override
    public ClientAnchor createClientAnctor(int rowIndex, short col, int row) {
        return new XSSFClientAnchor(0, 0, 512, 255, 0, rowIndex,col,row );
    }

    @Override
    public void setString(String val, Cell cell) {
        RichTextString richTextString = new XSSFRichTextString(val);
        cell.setCellValue(richTextString);
    }


    @Override
    public void setXSSFFillColor(CellStyle style, byte[] rgb) {
        XSSFColor xc = getXssfColor(rgb);
        XSSFCellStyle xssfCellStyle = (XSSFCellStyle)style;
        xssfCellStyle.setFillForegroundColor(xc);
        xssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    private XSSFColor getXssfColor(byte[] rgbColor){
        if(rgbColor == null || rgbColor.length != 3){
            return null;
        }
        Map<String, Color> _cs = localColors.get();

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

    @Override
    public void setXSSFFontColor(Font font, byte[] rgb) {
        XSSFFont xssfFont = (XSSFFont)font;
        XSSFColor xc = getXssfColor(rgb);
        xssfFont.setColor(xc);
    }

}
