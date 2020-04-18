package jk.core.hd;

import jk.core.excel.parse.base.Header;
import jk.core.excel.parse.base.ParseInfo;
import jk.core.excel.parse.base.ParseUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public class ExcelCommonHandle {

    private static final Logger logger = Logger.getLogger(ExcelCommonHandle.class);

    /**
     * 只做解析，并把解析结果通知使用者，不做数据结果存储
     * @param sheetName
     * @param curRow
     * @param rs
     */
    public static void sendParseResult(String sheetName, int curRow, Map rs, ParseInfo parseInfo, boolean onlyParse){
        if(onlyParse){
            //只做解析，并把解析结果通知使用者，不做数据结果存储
            if(parseInfo.getParseListener() != null){
                parseInfo.getParseListener().rowOpration(sheetName, curRow, rs, parseInfo.getHeaders(), parseInfo.getExtras());
            }else{
                logger.info("set as only parse data but not found any parseListener!!!!");
            }
        }
    }

    public static void rowHandle(List<RowDataHandle> rowDataHandles, String sheetName, int curRow, Map rs, Map<String, String> map, ParseInfo parseInfo) {
        if(rowDataHandles != null && rowDataHandles.size() > 0){
            StringBuffer msgBuffer = new StringBuffer();
            Object rowMsg = rs.get(CellDataHandle.INFO_NAME);
            if(!ParseUtils.isEmpty(rowMsg)){
                map.put(CellDataHandle.INFO_NAME, String.valueOf(rowMsg));
            }
            for(RowDataHandle hdl : rowDataHandles){
                String msg = hdl.exceute(sheetName, curRow, map, parseInfo.getHeaders(), parseInfo.getExtras());
                if(msg != null && msg.trim().length() > 0){
                    msgBuffer.append(msg).append(";");
                }
            }
            if(msgBuffer.length() > 0){
                if(rs.containsKey(RowDataHandle.INFO_NAME)){
                    rs.put(RowDataHandle.INFO_NAME, rs.get(RowDataHandle.INFO_NAME) + msgBuffer.toString());
                }else{
                    rs.put(RowDataHandle.INFO_NAME, msgBuffer);
                }
            }
        }
    }

    public static void cellHandle(int curRow, Map<String, String> map, Map rs, ParseInfo parseInfo, List<String> existList) {
        StringBuffer info = new StringBuffer();
        StringBuffer cellInfo = new StringBuffer();

        map.put(CellDataHandle.ROW_NUMBER_NAME, String.valueOf(curRow));

        for (Header h : parseInfo.getHeaders()) {
            if (h.getHandles() != null && h.getHandles().size() > 0) {
                StringBuffer msgBuffer = new StringBuffer();
                String val = map.get(h.getName());
                Object v = null;
                for(CellDataHandle hdl : h.getHandles()){
                    v = hdl.exceute(curRow, val, map, h, cellInfo, parseInfo.getExtras(), existList);
                    if(cellInfo.length() > 0) {
                        msgBuffer.append(cellInfo).append(",");
                        cellInfo.delete(0,cellInfo.length());
                    }
                }
                rs.put(h.getName(), v);
                if(msgBuffer.length() > 0) {
                    info.append(h.getExcelName() + ":" + msgBuffer.deleteCharAt(msgBuffer.length()-1)).append(";");
                }
            }
        }
        if (info.length() > 0) {
            rs.put(CellDataHandle.INFO_NAME, info);
        }
    }

    /**
     * 发送验证结果监听器
     * @param parseInfo
     * @param sheetName
     * @param curRow
     * @param rs
     */
    public static void sendEndLineParseResult(ParseInfo parseInfo, String sheetName, int curRow, Map rs) {
        if(parseInfo.getParseSheetListener() != null){
            parseInfo.getParseSheetListener().endParseOneRow(sheetName,curRow,rs,parseInfo.getHeaders(),parseInfo.getExtras());
        }
    }
}
