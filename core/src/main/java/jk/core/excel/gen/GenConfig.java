package jk.core.excel.gen;

import jk.core.ex.ExportException;
import jk.core.excel.cellinfo.ExcelInputStream;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * 导出信息配置
 * @ClassName GenConfig
 * @Description
 * @Version 1.0.0
 * @Author Jack lee
 * @Since 2020/4/18 下午7:17
 */
public class GenConfig {
    /**
     * 输出到流
     */
    private OutputStream out;
    /**
     * 需要输出的文件，与out一致
     */
    private File outFile;

    /**
     * 每个sheet的配置
     */
    private  List<SheetConfig> sheetConfigs;
    /**
     * 强制标记，区分是否为97及以前版本，一般在流的时候需要，文件时，自动识别
     * 默认为false，97以后版本
     */
    private boolean is97excel = false;
    /**
     * 处理的是否为大数据，即超过5万的方式，避免内存溢出，默认为true
     */
    private boolean largeData = true;
    /**
     * 需要采用的模板流
     */
    private ExcelInputStream excelInputStream;

    /***
     * 作为模板的文件名
     */
    private File inputTemplate;

    private GenConfig(){}

    /**
     * 创建一个新类
     * @return
     */
    public static GenConfig builder(){
        return new GenConfig();
    }


    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public File getOutFile() {
        return outFile;
    }

    public GenConfig setOutFile(File outFile) throws IOException {
        this.outFile = outFile;
        if(outFile == null){
            throw new ExportException("out file is null ");
        }
        if(!outFile.exists()){
            outFile.createNewFile();
        }
        String fileName = outFile.getName();
        fileName = fileName.toLowerCase();
        if(fileName.endsWith(".xls")){
            this.setIs97excel(true);
        }else{
            this.setIs97excel(false);
        }

        out = new FileOutputStream(outFile);

        return this;
    }

    public List<SheetConfig> getSheetConfigs() {
        return sheetConfigs;
    }

    public GenConfig setSheetConfigs(List<SheetConfig> sheetConfigs) {
        this.sheetConfigs = sheetConfigs;
        return this;
    }

    public boolean isIs97excel() {
        return is97excel;
    }

    public GenConfig setIs97excel(boolean is97excel) {
        this.is97excel = is97excel;
        return this;
    }

    public boolean isLargeData() {
        return largeData;
    }

    public GenConfig setLargeData(boolean largeData) {
        this.largeData = largeData;
        return this;
    }

    public ExcelInputStream getExcelInputStream() {
        return excelInputStream;
    }

    public GenConfig setExcelInputStream(ExcelInputStream excelInputStream) {
        this.excelInputStream = excelInputStream;
        return this;
    }

    public File getInputTemplate() {
        return inputTemplate;
    }

    public GenConfig setInputTemplate(File inputTemplate)  {
        this.inputTemplate = inputTemplate;
        if(inputTemplate == null || !inputTemplate.exists()){
            throw new ExportException("input template is null or not exists");
        }
        this.excelInputStream = new ExcelInputStream(inputTemplate);
        return this;
    }

    public GenConfig setInputTemplateFromUrl(URL inputTemplate)  {
        if(inputTemplate == null ){
            throw new ExportException("input template url is null ");
        }
        this.excelInputStream = new ExcelInputStream(inputTemplate);
        return this;
    }
}


