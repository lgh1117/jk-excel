package jk.demo.csv;

import jk.core.Excel;
import jk.core.ParseFactory;
import jk.core.excel.parse.base.Mapping;
import jk.core.excel.parse.base.ParseInfo;
import jk.demo.BaseTest;
import jk.demo.handle.DoubleHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class CsvParserNoHeaderTest extends BaseTest {

    public static void main(String[] args){
        File file = getFile("common_test_no_header.csv");
//        File file = getFile("performance_detail_110000_A10001_201805.csv");
        parse(file);
    }

    public static void parse(File file){
        //配置文件，同时指定数据开始行号，从1开始
        ParseInfo info = new ParseInfo(file, 1);
        info.setCsvFirstIsHeader(true);
        info.setCsvSeperator(",");
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
        Mapping m = null;
//        m =  new Mapping("year", "年",0);
//        list.add(m);
//        m = new Mapping("month", "月份",1);
//        list.add(m);
        m = new Mapping("name", "姓名",2);
        list.add(m);
        m = new Mapping("age", "年龄",3);
        m.addHandle(new DoubleHandler());
        list.add(m);
        m = new Mapping("weight", "体重",5);
        list.add(m);

        return  list;
    }

    public static List<Mapping> getPerMappings(){
        List<Mapping> list = new ArrayList<>();
        Mapping tradeNo = new Mapping("tradeNo","合同编号",2);
        list.add(tradeNo);
        Mapping perfType = new Mapping("perfType","业务类型",4);
        list.add(perfType);
        Mapping perfDate = new Mapping("perfDate","业务日期",8);
        list.add(perfDate);
        Mapping perfUserCode = new Mapping("perfUserCode","业绩归属人",11);
        list.add(perfUserCode);
        Mapping amount = new Mapping("amount","金额",13);
        list.add(amount);
        Mapping branchCode = new Mapping("branchCode","店组编码",14);
        list.add(branchCode);
        Mapping manager = new Mapping("manager","商圈经理工号",15);
        list.add(manager);
        Mapping areaCode = new Mapping("areaCode","区域编码",16);
        list.add(areaCode);
        Mapping areaManager = new Mapping("areaManager","区域经理工号",17);
        list.add(areaManager);
        return list;
    }
}
