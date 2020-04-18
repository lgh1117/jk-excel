package jk.core;

import jk.core.excel.gen.GenConfig;
import jk.core.excel.gen.HslfGenerator;
import jk.core.excel.gen.SxsslGenerator;
import jk.core.excel.gen.XsslGenerator;
import org.apache.log4j.Logger;

/**
 * @ClassName GenFactory
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 * @Since 2020/4/18 下午11:11
 */
public class GenFactory {
    private static final Logger logger = Logger.getLogger(GenFactory.class);

    private GenFactory(){}

    public static GenExcel createGenerator(GenConfig config){
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
