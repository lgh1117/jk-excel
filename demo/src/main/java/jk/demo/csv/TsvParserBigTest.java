package jk.demo.csv;


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
public class TsvParserBigTest extends BaseTest {

    public static void main(String[] args){
        File file = new File("/Users/liguohui/Downloads/sh_user_20180201/sh_user_20180201.csv");
        parse(file);
    }

    public static void parse(File file){
        //配置文件，同时指定数据开始行号，从1开始
        ParseInfo info = new ParseInfo(file, 1);
        info.setCsvSeperator(",");
        info.setMappings(getMappings());
        //获取解析器
        Excel excel = ParseFactory.getExcelParse(info);
        long l = System.currentTimeMillis();

        //开始解析数据
        List<Map> data = excel.parseToMapList();
        l = System.currentTimeMillis() - l;
//        for (Map m : data) {
//            System.out.println(m);
//        }
        System.out.println("all:" + data.size());
        System.out.println("time:" + l);
    }

    //配置映射
    public static List<Mapping> getMappings(){
        List<Mapping> list = new ArrayList<>();
        Mapping m = new Mapping("account", "account");
        list.add(m);
        m = new Mapping("anmeldenAddress", "anmeldenAddress");
        list.add(m);
        m = new Mapping("birthday", "birthday");
        list.add(m);
        m = new Mapping("school", "school");
        list.add(m);
        m = new Mapping("name", "name");
        list.add(m);
        m = new Mapping("aaaa", "rrrrr");
        list.add(m);

        return  list;
    }
}
