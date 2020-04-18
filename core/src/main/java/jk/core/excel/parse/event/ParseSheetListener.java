package jk.core.excel.parse.event;

import jk.core.excel.parse.base.Header;

import java.util.List;
import java.util.Map;

/**
 * Excel文件解析过程中的事件通知，sheet开始解析解析、头部解析结束、sheet解析结束三类事件
 * 只有Excel类型会触发此事件，其它文件类型不会触发，如CSV，TSV
 * Created by Jack.Lee
 */
public interface ParseSheetListener {

    /**
     * 开始解析某个sheet
     * @param sheetName
     */
    void startParseSheet(String sheetName);

    /**
     * 结束某个sheet头部解析
     * @param sheetName
     * @param headers
     */
    void endParseHeaders(String sheetName, List<Header> headers);

    /**
     * 结束某个sheet解析
     * @param sheetName
     * @param headers
     * @param datas
     * @param hasData
     * @param dataSize
     */
    void endParseSheet(String sheetName, List<Header> headers, List datas, boolean hasData, int dataSize);

    /**
     * <p>
     * Discription:行解析处理接口调用,返回处理结果，存放在返回解析的map中,
     * key为@jk.excel.parse.CellDataHandle.INFO_NAME
     * 如果返回的数据封装在一个实体类中，则提示信息不返回
     * </p>
     * @param sheetName
     * 			对应的sheet名称
     * @param rowIndex
     *            第几行
     * @param parseResult
     *            当前行通过RowDataHandle，CellDataHandle处理后的数据，
     *            里面包含有行错误信息 @jk.excel.parse.RowDataHandle.INFO_NAME
     *            ，单元格数据错误信息@jk.excel.parse.CellDataHandle.INFO_NAME
     * @param headers
     *            所有列头信息
     * @param extras
     *            存放外部信息
     * @return
     */
    void endParseOneRow(String sheetName, int rowIndex, Map parseResult, List<Header> headers, Map<String, Object> extras);

}
