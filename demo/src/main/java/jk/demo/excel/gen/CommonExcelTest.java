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
public class CommonExcelTest extends CreateBaseTest {

    public static void main(String[] args) {
        File file = getFile("CommonExcelTest-large.xlsx");
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
        return GenConfig.builder().setSheetConfigs(configs).setOutFile(file);
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

//        headerCellInfo.setColor();
        for (int i = 0; i < 10; i++) {
            h1 = new ExpHeader("这里是表头" + i, "val" + i, 1, 1, 1, i + 1);
            h1.setAutoWidth(true);
            h1.setHeadCellInfo(headerCellInfo);
            if (i == 5) {
                h1.setCellInfo(dataCellInfo);
            }
            headers.add(h1);
        }
        return headers;
    }
}
