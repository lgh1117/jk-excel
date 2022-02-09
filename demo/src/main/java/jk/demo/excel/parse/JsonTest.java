package jk.demo.excel.parse;

import jk.core.Excel;
import jk.core.ParseFactory;
import jk.core.excel.parse.base.ParseInfo;
import jk.demo.BaseTest;
import jk.demo.Person;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 通过配置json映射文件来解析excel
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class JsonTest extends BaseTest {

    public static void main(String[] args){
        File file = getFile("common_test.xlsx");
        parse(file);
        file = getFile("common_test.xls");
        parse(file);
    }

    public static void parse(File file){
        //配置文件，同时指定数据开始行号，从1开始
        ParseInfo info = new ParseInfo(file, 3);
        info.setMappingFile(getFile("person.json"));
        //获取解析器
        Excel excel = ParseFactory.getExcelParse(info);
        long l = System.currentTimeMillis();
        //开始解析数据
        List<Person> data = excel.parseToList(Person.class);
        l = System.currentTimeMillis() - l;
        for (Person m : data) {
            System.out.println(m);
        }
        System.out.println("all:" + data.size());
        System.out.println("time:" + l);
    }

}
