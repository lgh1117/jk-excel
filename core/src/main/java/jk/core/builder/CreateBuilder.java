package jk.core.builder;

import jk.core.GenExcel;
import jk.core.GenFactory;
import jk.core.ann.ExcelProperty;
import jk.core.csv.CsvConstants;
import jk.core.csv.CsvWriter;
import jk.core.ex.ExcelParseException;
import jk.core.ex.ExportException;
import jk.core.excel.cellinfo.ExcelInputStream;
import jk.core.excel.gen.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName CreateBuilder
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 * @Since 2022/2/6 下午11:12
 */
public class CreateBuilder {

    /**
     * 导出时的配置类
     */
    private GenConfig genConfig = GenConfig.builder();

    private SheetConfig sheetConfig = new SheetConfig();

    /**
     * 从第几行开始，一般是有导航、模板生成时使用到
     */
    private int startRowIndex = 0;

    private Class cls;

    private CreateBeforeAction action;

    private GenExcel excel;

    private CreateBuilder(){}

    public static CreateBuilder create(){
        return new CreateBuilder();
    }

    public static <T> CreateBuilder create(Class<T> cls){
        CreateBuilder b = new CreateBuilder();
        b.cls = cls;
        return b;
    }

    /**
     * 指定解析哪个sheet名称
     * @param sheetName
     * @return
     */
    public CreateBuilder sheetName(String sheetName){
        sheetConfig.setSheetName(sheetName);
        return this;
    }

    /**
     * 设置模板
     * @param tpl
     * @return
     */
    public CreateBuilder template(File tpl){
        if(tpl != null && tpl.exists() && tpl.canWrite() && tpl.canRead()){
            ExcelInputStream tplStream = new ExcelInputStream(tpl);
            genConfig.setExcelInputStream(tplStream);
        }
        return this;
    }

    /**
     * 从什么位置开始写数据
     * @param startRowIndex
     * @return
     */
    public CreateBuilder startRowIndex(int startRowIndex){
        this.startRowIndex = startRowIndex;
        return this;
    }

    public CreateBuilder template(ExcelInputStream tplStream){
        if(tplStream != null){
            genConfig.setExcelInputStream(tplStream);
        }
        return this;
    }

    public CreateBuilder out(File file) throws IOException {
        if(file == null){
            throw new ExcelParseException("不能为空:" + file);
        }
        String name = file.getName();
        boolean large = name.toLowerCase().endsWith(".xlsx") ? true : false;
        genConfig.setLargeData(large);
        genConfig.setOutFile(file);
        return this;
    }

    public CreateBuilder out(OutputStream out) throws FileNotFoundException {
        if(out == null){
            throw new ExcelParseException("输出流不能为空" );
        }
        this.genConfig.setOut(out);
        return this;
    }

    /**
     * 设置列头，如果没有设置，则根据提供的class解析
     * @param headers
     * @return
     */
    public CreateBuilder headers(List<ExpHeader> headers){
        sheetConfig.setHeaders(headers);
        return this;
    }

    /**
     * 设置在写出文件前的行为拦截
     * @param action
     * @return
     */
    public CreateBuilder beforeAction(CreateBeforeAction action){
        this.action = action;
        return this;
    }

    /**
     * 设置csv字段分割符
     * @param delimiter
     * @return
     */
    public CreateBuilder csvDelimiter(char delimiter){
        if(sheetConfig.getCsvFormat() == null){
            sheetConfig.setCsvFormat(CSVFormat.newFormat(delimiter));
        }else {
            CSVFormat format = sheetConfig.getCsvFormat().withDelimiter(delimiter);
            sheetConfig.setCsvFormat(format);
        }
        genConfig.setCsv(true);
        return this;
    }

    /**
     * 生成csv文件时，指明第一行是否需要有头部信息
     * @param firstHeader true 第一行表示头部，false不是
     * @return
     */
    public CreateBuilder csvFirstHeader(boolean firstHeader){
        CSVFormat format = sheetConfig.getCsvFormat();
        if(format == null){
            format = CSVFormat.newFormat(CsvConstants.COMMA).withSkipHeaderRecord(!firstHeader);
        }else{
            format = format.withSkipHeaderRecord(!firstHeader);
        }
        sheetConfig.setCsvFormat(format);
        genConfig.setCsv(true);
        return this;
    }

    /**
     * 需要到处的数据，与datasource必须有一个
     * @param data
     * @return
     */
    public CreateBuilder datas(List data){
        sheetConfig.setDatas(data);
        return this;
    }

    public CreateBuilder largeData(boolean largeData){
        genConfig.setLargeData(largeData);
        return this;
    }

    /**
     * 设置数据源，与datas必须有一个
     * @param datasource
     * @return
     */
    public CreateBuilder datasource(Datasource datasource){
        sheetConfig.setDatasource(datasource);
        return this;
    }

    /**
     * 指定生成的文件是csv文件
     * @param csv
     * @return
     */
    public CreateBuilder isCsv(boolean csv){
        genConfig.setCsv(true);
        return this;
    }

    public CreateBuilder write() throws IOException {
        buildWrite();
        excel = GenFactory.createGenerator(genConfig);
        excel.write();
        return this;
    }

    private void buildWrite() {
        try {
            if(StringUtils.isEmpty(sheetConfig.getSheetName())){
                sheetConfig.setSheetName("sheet1");
            }
            genConfig.addSheetConfig(sheetConfig);
            if(sheetConfig.getHeaders() != null && sheetConfig.getHeaders().size() > 0){
                //如果主动设置headers，则退出
                return;
            }
            if(cls == null){
                throw new ExcelParseException("没有提供头部配置");
            }
            buildHeader();
            if(sheetConfig.getHeaders() == null || sheetConfig.getHeaders().size() == 0){
                throw new ExcelParseException("没有提供头部配置");
            }

            if(this.genConfig.getOut() == null){
                throw new ExcelParseException("输出流不能为空");
            }
        }finally {
            if(this.action != null){
                this.action.exec(this);
            }
        }
    }

    public void close() throws ExportException {
        this.excel.close();
    }

    private void buildHeader() {
        Field[] fields = cls.getDeclaredFields();
        if(fields == null || fields.length == 0){
            return ;
        }
        List<ExpHeader> headers = new ArrayList<>();
        int _rowIndex = 1;
        boolean reCalColIndex = false;
        for(Field field : fields){
            ExcelProperty ann = field.getAnnotation(ExcelProperty.class);
            if(ann == null){
                continue;
            }
            String excelName = ann.name();
            if(StringUtils.isEmpty(excelName)){
                throw new ExcelParseException("类：" + cls + "对应的字段：" + field.getName() + " 未设置名称，与excel的列头一致");
            }
            String property = StringUtils.isEmpty(ann.property()) ? field.getName() : ann.property();
            int row = ann.row();
            row = row < 1 ? 1 : row;
            int rowIndex = ann.rowIndex();
            rowIndex = rowIndex < 1 ? _rowIndex : rowIndex;

            int col = ann.col();
            col = col < 1 ? 1 : col;
            int colIndex = ann.colIndex();
            if(colIndex < 1){
                reCalColIndex = true;
            }
            boolean autoWidth = ann.autoWidth();
            ExpHeader header = new ExpHeader(excelName, property, row, col, rowIndex, colIndex);
            header.setCsvIndex(ann.index());
            header.setAutoWidth(autoWidth);
            headers.add(header);
        }
        if(headers.size() == 0){
            return;
        }
        if(reCalColIndex) {
            Map<Integer, List<ExpHeader>> headerMap = headers.stream().collect(Collectors.groupingBy(ExpHeader::getRowIndex));
            headers.clear();
            for (Iterator<Map.Entry<Integer, List<ExpHeader>>> iter = headerMap.entrySet().iterator(); iter.hasNext(); ) {
                int _colIndex = 0;
                List<ExpHeader> hds = iter.next().getValue();
                for(ExpHeader header : hds){
                    header.setRowIndex(header.getRowIndex() + startRowIndex);
                    header.setColIndex(_colIndex);
                    headers.add(header);
                    _colIndex = _colIndex + header.getCol();
                }
            }
        }
        headers(headers);
    }

}
