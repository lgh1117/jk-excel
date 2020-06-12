package jk.demo.excel.gen;

import jk.core.GenExcel;
import jk.core.GenFactory;
import jk.core.excel.gen.ExpHeader;
import jk.core.excel.gen.GenConfig;
import jk.core.excel.gen.SheetConfig;
import jk.demo.CreateBaseTest;
import l.jk.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class MultiExcelTest extends CreateBaseTest {
    public static void main(String[] args){
        File file = getFile("MultiExcelTest.xlsx");
        createExcel(file);
        file = getFile("MultiExcelTest.xls");
        createExcel(file);
    }

    public static void createExcel(File file){
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
        List<ExpHeader> headers =  getHeaders();
        //设置配置
        SheetConfig config1 = new SheetConfig("测试sheet1", headers, datas);
        SheetConfig config2 = new SheetConfig("测试sheet2", headers, datas);
        //多sheet处理
        List<SheetConfig> configs = new ArrayList<SheetConfig>();
        configs.add(config1);
        configs.add(config2);
        return GenConfig.builder().setSheetConfigs(configs).setOutFile(file);
    }

    private static List<JSONObject> getDatas(){
        List<JSONObject> datas = new ArrayList<>();
        for(int j = 0 ; j < 5000 ; j++) {
            JSONObject d = new JSONObject();
            for (int i = 0; i < 10; i++) {
                d.put("val" + i, "val" + i);

            }
            datas.add(d);
        }
        return datas;
    }

    private static List<ExpHeader> getHeaders() {
        List<ExpHeader> headers = new ArrayList<ExpHeader>();
        ExpHeader h1 = null;
        for (int i = 0 ; i < 10 ; i++){
            h1 = new ExpHeader("name"+i, "val"+i, 1, 1, 1, i+1);
            headers.add(h1);
        }
        return headers;
    }
}
