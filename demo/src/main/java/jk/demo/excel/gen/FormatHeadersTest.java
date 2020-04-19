package jk.demo.excel.gen;


import jk.core.GenExcel;
import jk.core.GenFactory;
import jk.core.excel.cellinfo.CellInfo;
import jk.core.excel.cellinfo.Colors;
import jk.core.excel.gen.ExpHeader;
import jk.core.excel.gen.GenConfig;
import jk.core.excel.gen.SheetConfig;
import jk.demo.CreateBaseTest;
import org.apache.commons.math3.analysis.function.Exp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class FormatHeadersTest extends CreateBaseTest {
    public static void main(String[] args) {
        File file = getFile("format-headers.xlsx");
        createExcel(file);
        file = getFile("format-headers.xls");
        createExcel(file);
    }

    private static void createExcel(File file) {
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
        List<ExpHeader> headers = getExpHeaders();
        //设置配置
        SheetConfig config1 = new SheetConfig("填充特定表格", headers, datas);
        config1.setDataRowHeight(26);
        config1.setHeadRowHeight(35);

        //多sheet处理
        List<SheetConfig> configs = new ArrayList<SheetConfig>();
        configs.add(config1);
        return GenConfig.builder().setOutFile(file).setSheetConfigs(configs);
    }

    private static List<ExpHeader> getExpHeaders() {
        List<ExpHeader> headers = new ArrayList<>();
        int rowIndex = 1;
        int colIndex = 1;
        headers.add(new ExpHeader("姓名：", "", 1, 1, rowIndex, colIndex++));
        CellInfo cellInfo = new CellInfo();
        cellInfo.setBold(true);
        cellInfo.setColor(Colors.PINK);
        cellInfo.setFillHexColor("0000FF");
        ExpHeader header = new ExpHeader("张三", "", 1, 1, rowIndex, colIndex++);
        header.setHeadCellInfo(cellInfo);
        headers.add(header);
        headers.add(new ExpHeader("时间：", "", 1, 1, rowIndex, colIndex++));
        cellInfo = new CellInfo();
        cellInfo.setBold(true);
        cellInfo.setColor(Colors.RED);
        cellInfo.setFillHexColor("000000");
        header = new ExpHeader("2020-01-01 17:17:17", "", 1, 1, rowIndex, colIndex++);
        header.setHeadCellInfo(cellInfo);
        headers.add(header);

        rowIndex = 2;
        colIndex = 1;
        headers.add(new ExpHeader("班级：", "", 1, 1, rowIndex, colIndex++));
        cellInfo = new CellInfo();
        cellInfo.setBold(true);
        cellInfo.setColor(Colors.PINK);
        cellInfo.setFillHexColor("00FF00");
        header = new ExpHeader("一年级三班", "", 1, 1, rowIndex, colIndex++);
        header.setHeadCellInfo(cellInfo);
        headers.add(header);
        headers.add(new ExpHeader("人数：", "", 1, 1, rowIndex, colIndex++));
        cellInfo = new CellInfo();
        cellInfo.setBold(true);
        cellInfo.setColor(Colors.BLUE);
        cellInfo.setFillHexColor("BB33AA");
        header = new ExpHeader("100", "", 1, 1, rowIndex, colIndex++);
        header.setHeadCellInfo(cellInfo);
        headers.add(header);

        //数据域名

        rowIndex = 3;
        colIndex = 1;
        cellInfo = new CellInfo();
        cellInfo.setBold(true);
        cellInfo.setColor(Colors.WHITE);
        cellInfo.setFillHexColor("3CC43C");
        cellInfo.setBoldWeight(true);
        header = new ExpHeader("段值1", "val1", 1, 1, rowIndex, colIndex++);
        header.setHeadCellInfo(cellInfo);
        headers.add(header);
        header = new ExpHeader("段值2", "val2", 1, 1, rowIndex, colIndex++);
        header.setHeadCellInfo(cellInfo);
        headers.add(header);
        header = new ExpHeader("段值3", "val3", 1, 1, rowIndex, colIndex++);
        header.setHeadCellInfo(cellInfo);
        headers.add(header);
        header = new ExpHeader("段值4", "val4", 1, 1, rowIndex, colIndex++);
        header.setHeadCellInfo(cellInfo);
        headers.add(header);

        return headers;
    }

    private static List getDatas() {
        List<Map> datas = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Map d = new HashMap();
            for (int j = 0; j < 4; j++) {
                d.put("val" + (j + 1), "测试" + j * i);
            }
            datas.add(d);
        }
        return datas;
    }
}
