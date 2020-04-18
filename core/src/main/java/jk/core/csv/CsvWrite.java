package jk.core.csv;

import jk.core.GenExcel;
import jk.core.ex.ExportException;
import jk.core.excel.gen.GenConfig;
import org.apache.log4j.Logger;

/**
 * @ClassName CsvWrite
 * @Description
 * @Version 1.0.0
 * @Author Jack lee
 * @Since 2020/4/18 下午11:07
 */
public class CsvWrite implements GenExcel {

    private static final Logger logger = Logger.getLogger(CsvWrite.class);

    private GenConfig config;

    public CsvWrite(GenConfig config){
        this.config = config;
    }

    @Override
    public boolean write() throws ExportException {
        throw new ExportException("not support,todo.....");
    }
}
