package jk.demo.excel.parse;

import jk.core.Excel;
import jk.core.ParseFactory;
import jk.core.excel.parse.base.Header;
import jk.core.excel.parse.base.Mapping;
import jk.core.excel.parse.base.ParseInfo;
import jk.core.excel.parse.event.ParseSheetListener;
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
public class ParseSheetListenerTest extends BaseTest {

    public static void main(String[] args){
//        File file = getFile("common_sheets.xls");
//        File file = getFile("common_single.xls");
//        File file = getFile("common_single_empty.xls");
//        File file = getFile("common_test_sheets.xlsx");
        File file = getFile("common_test_single.xlsx");
//        File file = getFile("common_test_single_empty.xlsx");
        parse(file);
    }

    public static void parse(File file){
        //配置文件，同时指定数据开始行号，从1开始
        ParseInfo info = new ParseInfo(file, 3);
        info.setMappings(getMappings());
//        info.addSheet("person");
//        info.addSheet("other");
        info.setParseSheetListener(new MyParseSheetListener());
        //获取解析器
        Excel excel = ParseFactory.getExcelParse(info);
        long l = System.currentTimeMillis();
        //开始解析数据
        Map<String, List<Map>> datas = excel.parseAllSheetToMapList();
        l = System.currentTimeMillis() - l;
        if(datas != null) {
            for (Map.Entry<String, List<Map>> entry : datas.entrySet()) {
                List<Map> data = entry.getValue();
                for (Map m : data) {
                    System.out.println(m);
                }
                System.out.println("all:" + data.size());
            }
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
        m = new Mapping("person","weight", "体重");
        list.add(m);

        m = new Mapping("other","year", "年");
        list.add(m);
        m = new Mapping("other","month", "月份");
        list.add(m);
        return  list;
    }
}

class MyParseSheetListener implements ParseSheetListener {

    @Override
    public void startParseSheet(String sheetName) {
        System.out.println("startParseSheet listener:"+sheetName);

    }

    @Override
    public void endParseHeaders(String sheetName, List<Header> headers) {
        System.out.println("endParseHeaders listener:"+sheetName);
        System.out.println("endParseHeaders headers:"+headers);
    }

    @Override
    public void endParseSheet(String sheetName, List<Header> headers, List datas, boolean hasData, int dataSize) {
        System.out.println("endParseSheet listener:"+sheetName);
        System.out.println("endParseSheet hasData:"+hasData);
    }

    @Override
    public void endParseOneRow(String sheetName, int rowIndex, Map parseResult, List<Header> headers, Map<String, Object> extras) {
        System.out.println("endParseOneRow listener:"+sheetName);
    }
}