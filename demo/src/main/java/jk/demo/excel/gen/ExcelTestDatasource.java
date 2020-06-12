package jk.demo.excel.gen;

import jk.core.excel.gen.Datasource;
import jk.core.excel.gen.SheetConfig;
import l.jk.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class ExcelTestDatasource implements Datasource {


    private boolean next = true;
    private int index = 0;
    private long tag = 0;
    private boolean large;

    public ExcelTestDatasource(long tag,boolean large) {
        this.tag = tag;
        this.large = large;
    }

    /*
     * (non-Javadoc)
     *
     * @see jk.excel.ExcelDatasource#loadData(jk.excel.ExcelConfig)
     */
    @Override
    public List<JSONObject> loadData(SheetConfig config, int rows) {
        List<JSONObject> datas = new ArrayList<JSONObject>();
        int max = index + 800;
        if(!large && max > 65530){
            next = false;
            return Collections.emptyList();
        }
        for (int j = index; j < max; j++) {
            JSONObject json = new JSONObject();
            for (int i = 0; i <= 17; i++) {
                json.put("val" + i, tag + "val" + i);
            }
            datas.add(json);
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
