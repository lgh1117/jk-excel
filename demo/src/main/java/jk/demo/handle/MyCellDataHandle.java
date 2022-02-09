package jk.demo.handle;

import jk.core.excel.parse.base.Header;
import jk.core.hd.CellDataHandle;

import java.util.List;
import java.util.Map;

/**
 * @ClassName MyCellDataHandle
 * @Description
 * @Version 1.0.0
 * @Author liguohui
 * @Since 2021/10/21 下午5:09
 */
public class MyCellDataHandle implements CellDataHandle {
    @Override
    public Object exceute(int rowIndex, String value, Map row, Header header, StringBuffer info, Map<String, Object> extras, List<String> existList) {
        if(value != null && value.startsWith("1000-01-01")){
            return null;
        }
        return value;
    }
}
