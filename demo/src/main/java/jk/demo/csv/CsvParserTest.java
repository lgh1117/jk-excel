package jk.demo.csv;

import jk.core.Excel;
import jk.core.ParseFactory;
import jk.core.csv.CsvConstants;
import jk.core.excel.parse.base.Header;
import jk.core.excel.parse.base.Mapping;
import jk.core.excel.parse.base.ParseInfo;
import jk.core.excel.parse.event.ParseListener;
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
public class CsvParserTest extends BaseTest {

    public static void main(String[] args){
        File file = getFile("common_test.csv");
        parseListener(file);
        System.out.println("listener parse end......");
        parse(file);
        System.out.println("commonn parse end......");
    }

    public static void parseListener(File file){
        //配置文件，同时指定数据开始行号，从1开始
        ParseInfo info = new ParseInfo(file, 1);
        info.setCsvSeperator(CsvConstants.SEP_COMMA);
        info.setCsvFirstIsHeader(true);
        info.setMappings(getMappings());
        info.setParseListener(new TestParseListener());
        //获取解析器
        Excel excel = ParseFactory.getExcelParse(info);
        long l = System.currentTimeMillis();
        //开始解析数据
        excel.parse();
        System.out.println("time:" + (System.currentTimeMillis()-l));
    }
    public static void parse(File file){
        //配置文件，同时指定数据开始行号，从1开始
        ParseInfo info = new ParseInfo(file, 1);
        info.setCsvSeperator(CsvConstants.SEP_COMMA);
        info.setCsvFirstIsHeader(true);
        info.setMappings(getMappings());

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
        Mapping m = new Mapping("year", "年",0);
        list.add(m);
        m = new Mapping("month", "月份",1);
        list.add(m);
        m = new Mapping("name", "姓名",2);
        list.add(m);
        m = new Mapping("age", "年龄",3);
        list.add(m);
        return  list;
    }
}

class TestParseListener implements ParseListener {

    @Override
    public void rowOpration(String sheetName, int rowIndex, Map row, List<Header> headers, Map<String, Object> extras) {
        System.out.println(sheetName+"-->"+rowIndex+"-->"+row);
    }
}