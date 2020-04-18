package jk.demo.excel.parse;

import jk.core.Excel;
import jk.core.ParseFactory;
import jk.core.excel.parse.base.Mapping;
import jk.core.excel.parse.base.ParseInfo;
import jk.demo.BaseTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class CommonSheetsEmptyTest extends BaseTest {

    public static void main(String[] args){

        File file = getFile("common_sheets.xls");
        parse(file);
    }

    public static void parse(File file){
        //配置文件，同时指定数据开始行号，从1开始
        ParseInfo info = new ParseInfo(file, 3);
        info.setMappings(getMappings());
        //设置需要解析sheet名，不设置，默认为第一个sheet
        info.addSheet("person");
        info.addSheet("no header");
        info.setForceMatcher(true);
        //获取解析器
        Excel excel = ParseFactory.getExcelParse(info);
        long l = System.currentTimeMillis();
        //开始解析数据
        Map<String, List<Map>> data = excel.parseAllSheetToMapList();
        l = System.currentTimeMillis() - l;
        for(Iterator<String> iter = data.keySet().iterator(); iter.hasNext() ;) {
            String sheetName = iter.next();
            System.out.println("sheetname---"+sheetName);
            List<Map> list = data.get(sheetName);
            for (Map m : list) {
                System.out.println(m);
            }
        }
        System.out.println("all:" + data.size());
        System.out.println("time:" + l);
    }

    //配置映射
    public static List<Mapping> getMappings(){
        List<Mapping> list = new ArrayList<>();
        Mapping m = new Mapping("person","year", "年");
        list.add(m);
        m = new Mapping("person","month", "月份");
        list.add(m);
        m = new Mapping("person","name", "姓名");
        list.add(m);
        m = new Mapping("person","age", "年龄");
        list.add(m);

        m = new Mapping("no header","yearother", "年");
        list.add(m);
        m = new Mapping("no header","monthother", "月份");
        list.add(m);
        m = new Mapping("no header","nameother", "姓名");
        list.add(m);
        m = new Mapping("no header","ageother", "年龄");
        list.add(m);
        return  list;
    }
}
