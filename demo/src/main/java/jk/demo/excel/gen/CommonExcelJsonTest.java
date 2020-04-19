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
public class CommonExcelJsonTest extends CreateBaseTest {

    public static void main(String[] args){
        File file = getFile("CommonExcelTest-json.xlsx");
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
        File headerJson = getFileFromClasspath("parse_test.json");
        //设置配置
        SheetConfig config1 = new SheetConfig("这是一个测试", headerJson, datas);
        config1.setDataRowHeight(26);
        config1.setHeadRowHeight(35);

        //多sheet处理
        List<SheetConfig> configs = new ArrayList<SheetConfig>();
        configs.add(config1);
        return GenConfig.builder().setSheetConfigs(configs).setOutFile(file);
    }

    private static List<JSONObject> getDatas(){
        List<JSONObject> datas = new ArrayList<>();
        for(int j = 0 ; j < 100 ; j++) {
            JSONObject d = new JSONObject();
            d.put("name","中国"+j);
            d.put("age",j+30);
            datas.add(d);
        }
        return datas;
    }
}
