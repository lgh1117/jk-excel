package jk.core;

import jk.core.csv.CsvWriter;
import jk.core.ex.ExportException;
import jk.core.excel.gen.GenConfig;
import jk.core.excel.gen.HslfGenerator;
import jk.core.excel.gen.SxsslGenerator;
import jk.core.excel.gen.XsslGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @ClassName GenFactory
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 * @Since 2020/4/18 下午11:11
 */
public class GenFactory {
    private static final Logger logger = LogManager.getLogger(GenFactory.class);

    private GenFactory(){}

    public static GenExcel createGenerator(GenConfig config) {
        if(config.isCsv()){
            try {
                return new CsvWriter(config);
            }catch (Exception ex){
                throw new ExportException(ex.getMessage(),ex);
            }
        }
        if(config.isIs97excel()){
            if(config.isLargeData()){
                logger.warn("version 97 excel is not support argument largeData");
            }
            return new HslfGenerator(config);
        }else {
            if(config.isLargeData()){
                return new SxsslGenerator(config);
            }else{
                return new XsslGenerator(config);
            }
        }
    }
}
