package jk.demo.excel.parse;


import jk.core.builder.ParseBuilder;
import jk.core.excel.parse.base.FileType;
import jk.core.excel.parse.base.Mapping;
import jk.core.excel.parse.base.ParseInfo;
import jk.demo.BaseTest;
import jk.demo.Person;
import jk.demo.handle.MyExcelBuilderBeforAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CommonBeanTest
 * @Description
 * @Version 1.0.0
 * @Author liguohui
 * @Since 2021/6/17 下午9:21
 */
public class ParseBuilderTest extends BaseTest {

    public static void main(String[] args){
//        testBean();
//        testMap();
//        testCsv();
//        testNoHeaderCsv();
//        testHandleCsv();
        testOrderSheet();
    }

    public static void testHandleCsv(){
        File file = getFile("common_test.csv");
        List<Person> list = ParseBuilder.create(Person.class).dataFile(file).parseToBean();
        for(Person person : list) {
            System.out.println(person);
        }
    }


    public static void testCsv(){
        File file = getFile("common_test.csv");
        List<Person> list = ParseBuilder.create(Person.class).beforeParse(new MyExcelBuilderBeforAction()).dataFile(file).parseToBean();
        for(Person person : list) {
            System.out.println(person);
        }
    }

    private static List<Mapping> getMapping() {
        List<Mapping> rs =new ArrayList<>();
        rs.add(new Mapping("name","姓名"));
        rs.add(new Mapping("age","年龄"));
        rs.add(new Mapping("year","年"));
        rs.add(new Mapping("month","月份"));
        return rs;
    }

    public static void testNoHeaderCsv(){
        File file = getFile("common_no_header_test.csv");
        ParseBuilder builder = ParseBuilder.create().autoDetectorCharset().dataFile(file).mapping(getMappingNoHeader());
        ParseInfo info = builder.getParseInfo();
        info.setCsvFirstIsHeader(false);
        info.setFileType(FileType.TSV);
        List<Map> list = builder.parseToMapList();
        for(Map person : list) {
            System.out.println(person);
        }
    }
    private static List<Mapping> getMappingNoHeader() {
        List<Mapping> rs =new ArrayList<>();
        rs.add(new Mapping("column0","column0",0));
        rs.add(new Mapping("column1","column1",1));
        rs.add(new Mapping("column2","column2",2));
        rs.add(new Mapping("column3","column3",3));
        return rs;
    }

    private static void testOrderSheet(){
        File file = getFile("common_sheets.xlsx");
        String sheetName = "person";
        List<Map> list = ParseBuilder.create().dataFile(file).mapping(getSheetMappings()).parseAll().get(sheetName);
        for(Map d : list) {
            System.out.println(d);
        }
    }

    private static List<Mapping> getSheetMappings() {
        List<Mapping> rs =new ArrayList<>();
        rs.add(new Mapping("person","name","姓名"));
        rs.add(new Mapping("person","age","年龄"));
        rs.add(new Mapping("person","year","年"));
        rs.add(new Mapping("person","month","月份"));
        return rs;
    }


    public static void testBean(){
        File file = getFile("common_sheets.xlsx");
        List<Person> list = ParseBuilder.create(Person.class).dataIndex(3).dataFile(file).parseToBean();
        for(Person person : list) {
            System.out.println(person);
        }
    }
    public static void testMap(){
        File file = getFile("common_sheets.xlsx");
        List<Map> rs = ParseBuilder.create().mappingFile(getFile("person.json")).dataFile(file).parseToMapList();
        for(Map map : rs) {
            System.out.println(map);
        }
    }

}
