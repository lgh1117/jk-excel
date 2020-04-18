package jk.demo.excel.gen;


import jk.core.GenExcel;
import jk.core.GenFactory;
import jk.core.excel.cellinfo.CellInfo;
import jk.core.excel.cellinfo.Colors;
import jk.core.excel.gen.ExpHeader;
import jk.core.excel.gen.GenConfig;
import jk.core.excel.gen.SheetConfig;
import jk.demo.CreateBaseTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class DatasourceExcelTest extends CreateBaseTest {
    public static void main(String[] args){
        for(int i = 0 ; i < 2 ; i++) {
            File file = getFile("DatasourceExcelTest.xlsx");
            createExcel(file, 0);
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {

            }
            System.out.println("next times --------------");
        }
    }

    public static void createExcel(File file, long index) {
        try {
            GenConfig config = createConfig(file,index);
            GenExcel gen = GenFactory.createGenerator(config);
            gen.write();
            System.out.println("create end.....");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private static GenConfig createConfig(File file,long index) throws IOException {
        List<ExpHeader> headers = getHeaders();
        SheetConfig config = new SheetConfig("sheet test", headers,new ExcelTestDatasource(index));
        List<SheetConfig> configs = new ArrayList<SheetConfig>();
        configs.add(config);
        return GenConfig.builder().setSheetConfigs(configs).setOutFile(file);
    }


    private static List<ExpHeader> getHeaders() {
        // String name, String valueName, int row,int col, int rowIndex, int
        // colIndex
        CellInfo info = new CellInfo();
        info.setColor(Colors.RED);

        CellInfo info1 = new CellInfo();
        info1.setColor(Colors.RED);
        info1.setFillColor(Colors.LIGHT_ORANGE.getIndex());


        CellInfo headInfo = new CellInfo();
        headInfo.setFillColor(Colors.RED.getIndex());
        headInfo.setColor(Colors.YELLOW);

        CellInfo headInfo1 = new CellInfo();
        headInfo1.setFillColor(Colors.BLUE_GREY.getIndex());
        headInfo1.setColor(Colors.RED);

        List<ExpHeader> headers = new ArrayList<ExpHeader>();
        ExpHeader h1 = new ExpHeader("H1", "val1", 3, 1, 1, 1);
        headers.add(h1);
        h1.setCellInfo(info);
        ExpHeader h2 = new ExpHeader("H2", "val2", 3, 1, 1, 2);
        headers.add(h2);
        ExpHeader h3 = new ExpHeader("H3", "val3", 1, 2, 1, 3);
        headers.add(h3);
        ExpHeader h4 = new ExpHeader("H4", "val4", 2, 1, 2, 3);
        headers.add(h4);
        ExpHeader h5 = new ExpHeader("H5", "val5", 2, 1, 2, 4);
        headers.add(h5);
        ExpHeader h6 = new ExpHeader("H6", "val6", 1, 4, 1, 5);
        headers.add(h6);
        h6.setHeadCellInfo(headInfo1);
        ExpHeader h7 = new ExpHeader("H7", "val7", 1, 2, 2, 5);
        headers.add(h7);
        ExpHeader h8 = new ExpHeader("H8", "val8", 1, 2, 2, 7);
        headers.add(h8);
        ExpHeader h9 = new ExpHeader("H9", "val9", 1, 1, 3, 5);
        headers.add(h9);
        h9.setCellInfo(info1);
        ExpHeader h10 = new ExpHeader("H10", "val10", 1, 1, 3, 6);
        headers.add(h10);
        ExpHeader h11 = new ExpHeader("H11", "val11", 1, 1, 3, 7);
        headers.add(h11);
        ExpHeader h12 = new ExpHeader("H12", "val12", 1, 1, 3, 8);
        headers.add(h12);
        ExpHeader h13 = new ExpHeader("H13", "val13", 3, 1, 1, 9);
        headers.add(h13);
        h13.setHeadCellInfo(headInfo);
        ExpHeader h14 = new ExpHeader("H14", "val14", 2, 2, 1, 10);
        headers.add(h14);
        ExpHeader h15 = new ExpHeader("H15", "val15", 1, 1, 3, 10);
        headers.add(h15);
        ExpHeader h16 = new ExpHeader("H16", "val16", 1, 1, 3, 11);
        headers.add(h16);
        ExpHeader h17 = new ExpHeader("H17", "val17", 3, 1, 1, 12);
        h17.setHeadCellInfo(headInfo);
        headers.add(h17);
        return headers;
    }
}
