package jk.demo.excel.gen;

import jk.core.GenExcel;
import jk.core.GenFactory;
import jk.core.excel.cellinfo.CellInfo;
import jk.core.excel.cellinfo.Colors;
import jk.core.excel.gen.ExpHeader;
import jk.core.excel.gen.GenConfig;
import jk.core.excel.gen.SheetConfig;
import jk.demo.CreateBaseTest;
import jk.demo.excel.parse.Person;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class BeanToExcelTest extends CreateBaseTest {

    public static void main(String[] args){
        File file = getFile("BeanToExcelTest.xlsx");
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
        SheetConfig sheetConfig = new SheetConfig("这是一个测试", headers, datas);
        //多sheet处理
        List<SheetConfig> configs = new ArrayList<SheetConfig>();
        configs.add(sheetConfig);

        return GenConfig.builder().setSheetConfigs(configs).setOutFile(file);
    }

    private static List<Person> getDatas(){
        List<Person> datas = new ArrayList<>();
        for(int j = 0 ; j < 100 ; j++) {
            Person d = new Person();
            d.setAge(((long) (Math.random()*1000000000000l)));
            d.setMonth(((int) (Math.random()*11))+1);
            d.setName("name"+j);
            d.setYear(2019);
            d.setBirthday(new Date());
            datas.add(d);
        }
        return datas;
    }

    private static List<ExpHeader> getHeaders() {
        CellInfo info = new CellInfo();
        info.setColor(Colors.RED);
        info.setDataFormat("yyyy-MM-dd");

        CellInfo info1 = new CellInfo();
        info1.setColor(Colors.RED);
        info1.setFillColor(Colors.LIGHT_ORANGE.getIndex());
        info1.setDataFormatIndex(Integer.decode("0x31").shortValue());

        CellInfo info2 = new CellInfo();
        info2.setColor(Colors.BLUE);
        info2.setDataFormat("#,##0.00");


        CellInfo headInfo = new CellInfo();
        headInfo.setFillColor(Colors.RED.getIndex());
        headInfo.setColor(Colors.YELLOW);

        CellInfo headInfo1 = new CellInfo();
        headInfo1.setFillColor(Colors.BLUE_GREY.getIndex());
        headInfo1.setColor(Colors.RED);

        List<ExpHeader> headers = new ArrayList<ExpHeader>();
        ExpHeader h1 = null;
        h1 = new ExpHeader("年龄", "age", 1, 1, 1, 1);
        headers.add(h1);
        h1.setCellInfo(info1);
        h1 = new ExpHeader("姓名", "name", 1, 1, 1, 2);
        headers.add(h1);
        h1 = new ExpHeader("年份", "year", 1, 1, 1, 3);
        headers.add(h1);
        h1.setCellInfo(info2);
        h1 = new ExpHeader("月份", "month", 1, 1, 1, 4);
        headers.add(h1);

        h1 = new ExpHeader("生日", "birthday", 1, 1, 1, 5);
        headers.add(h1);
        h1.setCellInfo(info);
        return headers;
    }
}
