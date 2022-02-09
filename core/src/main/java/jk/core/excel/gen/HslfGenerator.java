package jk.core.excel.gen;

import jk.core.ex.ExcelParseException;
import jk.core.ex.ExportException;
import jk.core.excel.cellinfo.CellInfo;
import jk.core.excel.cellinfo.ExcelInputStream;
import jk.core.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生成2003版本的excel
 * @ClassName HslfGenerator
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 * @Since 2020/4/18 下午9:03
 */
public class HslfGenerator extends GenAbstract {

    private static final Logger logger = LogManager.getLogger(HslfGenerator.class);

    private GenConfig config ;
    private HSSFWorkbook workbook;
    private InputStream inputStream ;

    public HslfGenerator(GenConfig config){
        super(config);
        this.config = config;
    }

    @Override
    public boolean write(OutputStream out, List<SheetConfig> configs, ExcelInputStream excelInputStream) throws ExportException {
        try {
            if (Utils.isEmpty(out)) {
                throw new ExportException("excel文件输出流为空！");
            }
            inputStream = getInputStream(excelInputStream);
            createWorkbook(configs, inputStream);
            workbook.write(out);
            return true;
        }catch (Exception ex){
            throw new ExportException(ex.getMessage(),ex);
        }
    }

    @Override
    public void close() throws ExportException {
        if(config.getOut() != null){
            try {
                config.getOut().flush();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        if(workbook != null) {
            try {
                workbook.close();
            } catch (Exception e) {
                throw new ExportException(e.getMessage(), e);
            }
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

    private void createWorkbook(List<SheetConfig> sheetConfigs, InputStream inputStream)throws IOException {
        if(inputStream != null){
            workbook = new HSSFWorkbook(inputStream);
        }else {
            workbook = new HSSFWorkbook();
        }
        if (!Utils.isEmptyCollection(sheetConfigs)) {
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
    }


    private void setHssFRgbColor(HSSFWorkbook workbook, List<SheetConfig> sheetConfigs) {
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
     * 生成指定的颜色到workbook中
     * @param workbook
     * @param existIndexs
     * @param changeColors
     * @param colorIndexMapping
     */
    private void changeHSSFColor(HSSFWorkbook workbook, List<Short> existIndexs
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
                logger.warn("还有【"+changeColors.size()+"】类颜色未处理，它们的设置将不会生效");
            }
        }

    }

    /**
     * 如果color或者fillColor未设置，而rgbColor设置，则更新对应color或fillColor
     * @param sheetConfigs
     * @param colorIndexMapping
     */
    private void changeColorIndex(List<SheetConfig> sheetConfigs, Map<String, Integer> colorIndexMapping) {
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

    private void _changeColorIndex(CellInfo info, Map<String, Integer> colorIndexMapping){
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

    @Override
    public DataValidation createValidation(ExpHeader col, Sheet sheet) {
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

    @Override
    public void checkRowIndex(int rowIndex) {
        int maxIndex = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if(rowIndex > maxIndex){
            throw new ExcelParseException("待导出数据["+rowIndex+"]大于允许导出的最大行数["+maxIndex+"]");
        }
    }

    @Override
    public ClientAnchor createClientAnctor(int rowIndex, short col, int row) {
        return new HSSFClientAnchor(0, 0, 512, 255, (short) 0, rowIndex,col, row );
    }

    @Override
    public void setString(String val, Cell cell) {
        HSSFRichTextString richTextString = new HSSFRichTextString(val);
        cell.setCellValue(richTextString);
    }

    @Override
    public void flushRows(Sheet sheet) {
        //nothing todo
    }

    @Override
    public void setXSSFFillColor(CellStyle style, byte[] fillRgbColor) {
        //nothing todo
    }

    @Override
    public void setXSSFFontColor(Font font, byte[] rgbColor) {
        //nothing todo
    }
}
