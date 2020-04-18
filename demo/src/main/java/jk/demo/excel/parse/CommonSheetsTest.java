package jk.demo.excel.parse;

import jk.core.Excel;
import jk.core.ParseFactory;
import jk.core.excel.parse.base.Mapping;
import jk.core.excel.parse.base.ParseInfo;
import jk.demo.BaseTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class CommonSheetsTest extends BaseTest {

    public static void main(String[] args){

        File file = getFile("common_sheets.xls");
        parse(file);
    }

    public static void parse(File file){
        //配置文件，同时指定数据开始行号，从1开始
        ParseInfo info = new ParseInfo(file, 3,true);
        info.setMappings(getMappings());
        //获取解析器
        Excel excel = ParseFactory.getExcelParse(info);
        long l = System.currentTimeMillis();
        //开始解析数据
       Map<String, List<Map>> datas = excel.parseAllSheetToMapList();
        l = System.currentTimeMillis() - l;
        for(Map.Entry<String, List<Map>> entry : datas.entrySet()) {
            List<Map> data = entry.getValue();
            for (Map m : data) {
                System.out.println(m);
            }
            System.out.println("all:" + data.size());
        }

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

        m = new Mapping("other1","yearother", "年2");
        list.add(m);
        m = new Mapping("other1","monthother", "月份");
        list.add(m);
        m = new Mapping("other1","nameother", "姓名");
        list.add(m);
        m = new Mapping("other1","ageother", "年龄");
        list.add(m);
        return  list;
    }
}
