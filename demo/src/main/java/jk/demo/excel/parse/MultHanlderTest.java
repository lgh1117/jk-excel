package jk.demo.excel.parse;



import jk.core.Excel;
import jk.core.ParseFactory;
import jk.core.excel.parse.base.Mapping;
import jk.core.excel.parse.base.ParseInfo;
import jk.demo.BaseTest;
import jk.demo.handle.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 多个行处理器，rowHandler
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class MultHanlderTest extends BaseTest {

    public static void main(String[] args){
        File file = getFile("common_test.xlsx");
        parse(file);
        file = getFile("common_test.xls");
        parse(file);
    }

    public static void parse(File file){
        //配置文件，同时指定数据开始行号，从1开始
        ParseInfo info = new ParseInfo(file, 3);
        info.setMappings(getMappings());
        info.addRowHandles(new Row1Handler(),new Row2Handler());
        //获取解析器
        Excel excel = ParseFactory.getExcelParse(info);
        long l = System.currentTimeMillis();
        //开始解析数据
        List<Map> data = excel.parseToMapList();
        l = System.currentTimeMillis() - l;
        for (Map m : data) {
            System.out.println(m);
        }
        System.out.println("all:" + data.size());
        System.out.println("time:" + l);
    }

    //配置映射
    public static List<Mapping> getMappings(){
        List<Mapping> list = new ArrayList<>();
        Mapping m = new Mapping("year", "年");
        list.add(m);
        m = new Mapping("month", "月份");
        list.add(m);
        m = new Mapping("name", "姓名");
        list.add(m);
        m = new Mapping("age", "年龄");
        list.add(m);
        m = new Mapping("height", "身高");
        m.addHandles(new DoubleHandler(),new EmptyHandler());
        list.add(m);
        m = new Mapping("weight", "体重");
        m.setHandle(new BigDecimalHandler());
        list.add(m);
        return  list;
    }
}
