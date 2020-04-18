package jk.demo.handle;


import jk.core.excel.parse.base.Header;
import jk.core.hd.RowDataHandle;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class Row1Handler implements RowDataHandle {
    @Override
    public String exceute(String sheetName, int rowIndex, Map row, List<Header> headers, Map<String, Object> extras) {
        return "row1 handler";
    }
}
