package jk.demo.excel.gen;


import jk.core.GenExcel;
import jk.core.GenFactory;
import jk.core.excel.gen.ExpHeader;
import jk.core.excel.gen.GenConfig;
import jk.core.excel.gen.SheetConfig;
import jk.demo.CreateBaseTest;

import java.io.File;
import java.io.FileInputStream;
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
public class TplCommonExcelTest extends CreateBaseTest {
    public static void main(String[] args){
        File file = getFile("excel-tpl-test.xlsx");
        File tpl = getFile("excel-tpl.xlsx");
        createExcel(file,tpl);
        file = getFile("excel-tpl-test.xls");
        tpl = getFile("excel-tpl.xls");
        createExcel(file,tpl);
    }

    public static void createExcel(File file, File tpl){
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
        SheetConfig config = new SheetConfig("sheet", headers, datas);
        config.setDataRowHeight(26);
        config.setHeadRowHeight(35);
        List<SheetConfig> configs = new ArrayList<SheetConfig>();
        configs.add(config);
        return GenConfig.builder().setOutFile(file).setSheetConfigs(configs);
    }

    private static List<ExpHeader> getExpHeaders() {
        List<ExpHeader> headers = new ArrayList<>();
        int rowIndex = 6;
        int colIndex = 1;
        //数据域名

        headers.add(new ExpHeader("段值1","val1",1,1,rowIndex,colIndex++));
        headers.add(new ExpHeader("段值2","val2",1,1,rowIndex,colIndex++));
        headers.add(new ExpHeader("段值3","val3",1,1,rowIndex,colIndex++));

        return headers;
    }

    private static List getDatas() {
        List<Map> datas = new ArrayList<>();
        for(int i = 0 ; i < 1000 ; i++){
            Map d = new HashMap();
            for(int j = 0 ; j < 3 ; j++){
                d.put("val" + (j+1) , "测试" + j * i);
            }
            datas.add(d);
        }
        return datas;
    }
}
