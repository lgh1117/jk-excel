package jk.demo;

import java.io.File;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class CreateBaseTest {
    public static File getFile(String filename){
//        URL url = BaseTest.class.getClassLoader().getResource("xls");
//        if(url != null){
            File file = new File("test/xls");
            if(!file.exists()){
                file.mkdirs();
            }
            return new File(file,filename);
//        }
//        throw new RuntimeException("not found file");
    }
}
