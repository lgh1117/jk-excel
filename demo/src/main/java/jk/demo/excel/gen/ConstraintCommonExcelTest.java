package jk.demo.excel.gen;

import com.alibaba.fastjson.JSONObject;
import jk.core.GenExcel;
import jk.core.GenFactory;
import jk.core.excel.cellinfo.CellInfo;
import jk.core.excel.gen.ExpHeader;
import jk.core.excel.gen.GenConfig;
import jk.core.excel.gen.SheetConfig;
import jk.demo.CreateBaseTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class ConstraintCommonExcelTest extends CreateBaseTest {

    public static void main(String[] args) {
//        File file = getFile("CommonExcelTest-constraint-large.xlsx");
//        File file = getFile("CommonExcelTest-constraint.xlsx");
        File file = getFile("CommonExcelTest-constraint.xls");
        createExcel(file);

    }

    public static void createExcel(File file) {
        try {
            GenConfig config = createConfig(file);
            GenExcel gen = GenFactory.createGenerator(config);
            gen.write();
            System.out.println("create end.....");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static GenConfig createConfig(File file) throws IOException {
        //产生数据
        List datas = getDatas();
        //设置头部对应关系
        List<ExpHeader> headers = getHeaders();
        //设置配置
        SheetConfig config1 = new SheetConfig("这是一个测试", headers, datas);
        config1.setDataRowHeight(26);
        config1.setHeadRowHeight(35);

        //多sheet处理
        List<SheetConfig> configs = new ArrayList<SheetConfig>();
        configs.add(config1);
        return GenConfig.builder().setOutFile(file).setSheetConfigs(configs);
    }

    private static List<JSONObject> getDatas() {
        List<JSONObject> datas = new ArrayList<>();
        for (int j = 0; j < 100; j++) {
            JSONObject d = new JSONObject();
            for (int i = 0; i < 10; i++) {
                d.put("val" + i, "这个是一个测试的地方是电风扇val" + i);

            }
            datas.add(d);
        }
        return datas;
    }

    private static List<ExpHeader> getHeaders() {
        List<ExpHeader> headers = new ArrayList<ExpHeader>();
        ExpHeader h1 = null;
        //字体大小、颜色、行高
        CellInfo headerCellInfo = new CellInfo();
        headerCellInfo.setFontHightInPoints(Short.valueOf("16"));
//        headerCellInfo.setFillColor((short)64);
        headerCellInfo.setHexColor("19a0ef");

        CellInfo dataCellInfo = new CellInfo();
        dataCellInfo.setWrapText(true);
        dataCellInfo.setFillHexColor("ABABAB");
        dataCellInfo.setHexColor("FF00AA");

        List<String> constrainData = new ArrayList<>();
        constrainData.add("jk");
        constrainData.add("李国辉");
        constrainData.add("贝壳");
        constrainData.add("lianjia");
//        headerCellInfo.setColor();
        for (int i = 0; i < 10; i++) {
            h1 = new ExpHeader("这里是表头" + i, "val" + i, 1, 1, 1, i + 1);
            h1.setAutoWidth(true);
            h1.setHeadCellInfo(headerCellInfo);
            if (i == 5) {
                h1.setCellInfo(dataCellInfo);
            }
            if (i == 2 || i == 5) {
                h1.setConstraintData(constrainData);
            }
            headers.add(h1);
        }
        return headers;
    }


    private static List<ExpHeader> getExpHeaders() {
        CellInfo cellInfo = new CellInfo();
        cellInfo.setFillHexColor("91D5FF");

        CellInfo info = new CellInfo();
        info.setFillHexColor("FF00AA");
        info.setFontName("华文行楷");

        int columnCount = 1;
        List<ExpHeader> res = new ArrayList<>();
        ExpHeader header = new ExpHeader("门店编码", "storeCode", 2, 1, 1, columnCount++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(info);
        res.add(header);
        header = new ExpHeader("店组编码", "branchCode", 2, 1, 1, columnCount++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("门店状态", "storeState", 2, 1, 1, columnCount++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("管理大部", "operationOrgName", 2, 1, 1, columnCount++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("大区", "areaName", 2, 1, 1, columnCount++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("门店名称", "branchName", 2, 1, 1, columnCount++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("业绩", "incomeAmount", 2, 1, 1, columnCount++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);


        header = new ExpHeader("其他业绩", "xx", 2, 1, 1, columnCount++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("利润", "totalAmount", 2, 1, 1, columnCount++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("当前负债", "debtAmount", 2, 1, 1, columnCount++); //todo
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("成本合计", "costAmount", 2, 1, 1, columnCount++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);

        int humanColumnCount = 0;
        int storeColumnCount = 0;
        int tradeColumnCount = 0;
        int backStageColumnCount = 0;


        int humanindex = columnCount;
        int storeindex = humanindex + humanColumnCount + 3;
        int tradeindex = storeindex + storeColumnCount + 3;
        int backStageindex = tradeindex + tradeColumnCount + 3;

        //一行大项
        header = new ExpHeader("人力成本", "人力成本", 1, humanColumnCount + 3, 1, humanindex);
//        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("店面成本", "店面成本", 1, storeColumnCount + 3, 1, storeindex);
//        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("交易成本", "交易成本", 1, tradeColumnCount + 3, 1, tradeindex);
//        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("中后台成本", "中后台成本", 1, backStageColumnCount + 3, 1, backStageindex);
//        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);

        header = new ExpHeader("合计", "a", 1, 1, 2, humanindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("合计", "b", 1, 1, 2, storeindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("合计", "c", 1, 1, 2, tradeindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("合计", "d", 1, 1, 2, backStageindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);

        header = new ExpHeader("占总成本比", "e", 1, 1, 2, humanindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("占总成本比", "f", 1, 1, 2, storeindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("占总成本比", "g", 1, 1, 2, tradeindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("占总成本比", "h", 1, 1, 2, backStageindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);

        header = new ExpHeader("占业绩比", "i", 1, 1, 2, humanindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("占业绩比", "j", 1, 1, 2, storeindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("占业绩比", "k", 1, 1, 2, tradeindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);
        header = new ExpHeader("占业绩比", "l", 1, 1, 2, backStageindex++);
        header.setAutoWidth(true);
        header.setHeadCellInfo(cellInfo);
        res.add(header);


        return res;
    }


}
