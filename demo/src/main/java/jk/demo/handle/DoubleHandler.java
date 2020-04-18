package jk.demo.handle;


import jk.core.excel.parse.base.Header;
import jk.core.hd.CellDataHandle;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class DoubleHandler implements CellDataHandle {
    @Override
    public Object exceute(int rowIndex, String value, Map row, Header header, StringBuffer info, Map<String, Object> extras, List<String> existList) {
        if(value == null){
            return null;
        }
        try {
            return Double.parseDouble(value);
        }catch (Exception e){
            info.append("不是double类型");
            //e.printStackTrace();
        }
        return null;
    }
}
