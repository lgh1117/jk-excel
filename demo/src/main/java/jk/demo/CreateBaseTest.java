package jk.demo;


import java.io.File;
import java.net.URL;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class CreateBaseTest {

    public static File getFile(String filename){
        File file = new File("test/xls");
        if(!file.exists()){
            file.mkdirs();
        }
        return new File(file,filename);
    }

    public static File getFileFromClasspath(String filename){
        URL url = BaseTest.class.getClassLoader().getResource("gen/" +filename);
        if(url != null){
            return new File(url.getFile());
        }
        return null;
    }
}
