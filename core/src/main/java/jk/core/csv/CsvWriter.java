package jk.core.csv;

import jk.core.GenExcel;
import jk.core.ex.ExportException;
import jk.core.excel.gen.Datasource;
import jk.core.excel.gen.ExpHeader;
import jk.core.excel.gen.GenConfig;
import jk.core.excel.gen.SheetConfig;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName CsvWrite
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 * @Since 2022/2/6 下午11:07
 */
public class CsvWriter implements GenExcel {
    private static final Logger log = LogManager.getLogger(CsvWriter.class);

    private CSVPrinter printer;
    private GenConfig config;
    private SheetConfig sheetConfig;
    private final Appendable out;
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public CsvWriter(GenConfig config) throws IOException {
        this.config = config;
        if(config.getOut() != null) {
            this.out = new OutputStreamWriter(config.getOut());
        }else if(config.getOutFile() != null && config.getOutFile().exists() && config.getOutFile().canWrite()){
            this.out = new FileWriter(config.getOutFile(), true);
        }else{
            throw new ExportException("未找到任何输出流");
        }
        initPrinter();
    }

    private void initPrinter() throws IOException {
        if(this.config.getSheetConfigs().size() != 1){
            throw new ExportException("when create csv file,the sheet config must be one configuration!! ");
        }
        this.sheetConfig = this.config.getSheetConfigs().get(0);
        initFormat();
        printer = new CSVPrinter(out,sheetConfig.getCsvFormat());
    }

    private void initFormat() {
        CSVFormat format = sheetConfig.getCsvFormat();
        if(format == null){
            format = CSVFormat.newFormat(CsvConstants.COMMA);
        }
        setHeaders(format);
    }

    private void setHeaders(CSVFormat format) {
        List<ExpHeader> headers = sheetConfig.getHeaders();
        String[] csvHeaders = new String[headers.size()];
        List<ExpHeader> sorted = headers.stream().filter(h -> h.getCsvIndex() > 0).collect(Collectors.toList());
        if(sorted != null && sorted.size() > 0){
            Collections.sort(headers,(o1, o2) -> {
                return o1.getCsvIndex() - o2.getCsvIndex();
            });
        }
        for(int i = 0 ; i < headers.size() ; i++){
            csvHeaders[i] = headers.get(i).getName();
        }
        format = format.withHeader(csvHeaders).withRecordSeparator("\n");
        sheetConfig.setCsvFormat(format);
    }

    @Override
    public boolean write() throws ExportException {
        try {
            if (sheetConfig.getDatas() != null && sheetConfig.getDatas().size() > 0) {
                printData(sheetConfig.getDatas());
                printer.flush();
            } else if (sheetConfig.getDatasource() != null) {
                Datasource datasource = sheetConfig.getDatasource();
                int currRows = 0;
                while (datasource.hasNext()) {
                    List datas = datasource.loadData(sheetConfig, currRows);
                    if (datas != null && datas.size() > 0) {
                        printData(datas);
                        currRows = currRows + datas.size();
                        printer.flush();
                    }
                }
            } else {
                log.warn("warning !!!! not found any data to write!!!");
                return false;
            }
            return true;
        }catch (Exception ex){
            throw new ExportException(ex.getMessage(),ex);
        }
    }

    private void printData(List datas) throws IOException {
        for(Object data : datas) {
            Object[] values = new Object[sheetConfig.getHeaders().size()];
            int sn = 0;
            for (ExpHeader header : sheetConfig.getHeaders()) {
                if (header != null) {
                    Object value = getValue(data, header);
                    values[sn++] = value;
                }
            }
            printer.printRecord(values);
        }
    }

    /**
     * @param data
     * @param header
     * @return
     */
    private Object getValue(Object data, ExpHeader header) {
        if(data == null || header == null){
            return null;
        }

        if(data instanceof Map){
            Map _map = (Map)data;
            return _map.containsKey(header.getValueName()) ? _map.get(header.getValueName()) : null;
        }
        Object val = getValue(data,header.getValueName());
        if(val != null) {
            if (val instanceof Date) {
                return sdf.format(val);
            }
        }
        return val;
    }

    /**
     *
     * @param data
     * @param field
     * @return
     */
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
            log.debug(field+"--"+e.getMessage(),e);
        }
        return null;
    }

    /**
     *
     * @param cls
     * @param field
     * @return
     * @throws SecurityException
     */
    private static Method getMethod(Class<? extends Object> cls, String field) throws  SecurityException {
        if(cls == Object.class){
            return null;
        }
        String methodName =field.length() >1 ?  "get"+(field.substring(0,1).toUpperCase())+field.substring(1)
                : "get"+(field.substring(0,1).toUpperCase());
        Method m = null;
        try {
            m = cls.getDeclaredMethod(methodName);
        } catch (Exception e) {
            // TODO: handle exception
        }
        if(m == null){
            return getMethod(cls.getSuperclass(),field);
        }
        return m;
    }

    public void close() throws ExportException {
        try {
            printer.flush();
            printer.close();
        }catch (Exception ex){
            throw new ExportException(ex.getMessage(),ex);
        }
    }
}
