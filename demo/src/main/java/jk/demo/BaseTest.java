package jk.demo;


import org.apache.logging.log4j.core.config.ConfigurationFactory;

import java.io.File;
import java.net.URL;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class BaseTest {

    static {
//        System.setProperty(ConfigurationFactory.CONFIGURATION_FILE_PROPERTY,"log4j2.xml");
    }

    public static File getFile(String filename)  {
        URL url = BaseTest.class.getClassLoader().getResource("parse/" +filename);
        if(url != null){
            return new File(url.getFile());
        }
        return null;
    }

}
