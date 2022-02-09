package jk.demo.excel.gen;

import jk.core.excel.gen.Datasource;
import jk.core.excel.gen.SheetConfig;
import jk.demo.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by liguohui on 2018/2/7.
 */
public class BuilderDatasource implements Datasource {


    private boolean next = true;
    private int index = 0;

    public BuilderDatasource() {
    }

    @Override
    public List loadData(SheetConfig sheetConfig, int rows) {
        List datas = new ArrayList();
        int max = index + 800;
        for (int j = index; j < max; j++) {
            datas.add(new Person((new Random().nextInt(2021)),new Random().nextInt(12),(long)(new Random().nextInt(100)),"name" + j,new Date()));
        }
        System.out.println("当前生成数据所处行数："+index);
        if (max < 1000000) {
            next = true;
        } else {
            next = false;
        }
        index = max;
        return datas;
    }

    /*
     * (non-Javadoc)
     *
     * @see jk.excel.ExcelDatasource#hasNext()
     */
    @Override
    public boolean hasNext() {
        return next;
    }
}
