package jk.core.hd;

import java.util.Map;

/**
 * 只能本包使用
 * @ClassName ExtraCellDataHandle
 * @Description
 * @Version 1.0.0
 * @Author liguohui
 * @Since 2020/5/14 下午5:07
 */
public interface ExtraCellDataHandle {

    /**
     * 除有效数据外的数据处理器
     * @param sheetIndex sheet序列号,从0开始
     * @param sheetName sheet名称
     * @param rowIndex 行号，从1开始
     * @param colIndex 列号，从0开始
     * @param value 单元格值
     * @param extras 传入的额外信息
     */
    void optRows(int sheetIndex, String sheetName, int rowIndex, int colIndex, Object value, Map<String, Object> extras);

}
