package jk.demo;

import org.apache.log4j.BasicConfigurator;

import java.io.File;
import java.net.URL;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class BaseTest {
    static {
        BasicConfigurator.configure();
    }

    public static File getFile(String filename)  {
        URL url = BaseTest.class.getClassLoader().getResource("xls/"+filename);
        if(url != null){
            return new File(url.getFile());
        }
        return null;
    }

}
