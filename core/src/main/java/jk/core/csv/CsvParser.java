package jk.core.csv;

import jk.core.Excel;
import jk.core.excel.parse.base.Header;
import jk.core.ex.ExcelParseException;
import jk.core.excel.parse.base.ParseInfo;
import jk.core.excel.parse.base.ParserUtil;
import jk.core.hd.CellDataHandle;
import jk.core.hd.ExcelCommonHandle;
import jk.core.util.RegExpUtil;
import jk.core.util.Utils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * 解析csv、tsv等文件
 *  @author Jack lee
 *  @since 2020.04.15
 */
public class CsvParser implements Excel {
    private static final Logger logger = Logger.getLogger(CsvParser.class);
    private ParseInfo parseInfo;

    private Map<String, List<Map>> dataMap;

    private String fileName;

    private List<String> existList;

    private boolean onlyParse = false;

    public CsvParser(ParseInfo info){
        parseInfo = info;
        dataMap = new HashMap<>();
    }

    @Override
    public List<Map> parseToMapList() {
        try {
            process();
            if(dataMap == null || dataMap.size() == 0){
                logger.warn("the file " + parseInfo.getFile().getName() + " is not data!!! ");
                return Collections.emptyList();
            }
            Iterator<List<Map>> iter = dataMap.values().iterator();
            if(!iter.hasNext()){
                return Collections.emptyList();
            }
            return iter.next();
        } catch (Exception e) {
            throw new ExcelParseException(e.getMessage(), e);
        }
    }

    @Override
    public <T> List<T> parseToList(Class<T> t) {
        try {
            process();
            if(dataMap == null || dataMap.size() == 0){
                logger.warn("the file " + parseInfo.getFile().getName() + " is not data!!! ");
                return Collections.emptyList();
            }
            Iterator<List<Map>> iter = dataMap.values().iterator();
            if(!iter.hasNext()){
                return Collections.emptyList();
            }
            List<Map> data = iter.next();
            return ParserUtil.convertData(data, t);
        } catch (Exception e) {
            throw new ExcelParseException(e.getMessage(), e);
        }
    }

    @Override
    public Map<String, List<Map>> parseAllSheetToMapList() {
        try {
            process();
            return dataMap;
        } catch (Exception e) {
            throw new ExcelParseException(e.getMessage(), e);
        }
    }

    @Override
    public void parse() {
        try {
            onlyParse = true;
            process();
        } catch (Exception e) {
            throw new ExcelParseException(e.getMessage(), e);
        }
    }

    /**
     * 该方法未校验头部与数据一致性 todo
     * @throws IOException
     */
    private void process() throws IOException{
        try(CSVParser p = getRecordIterator()){
            Iterable<CSVRecord> records = p;
            List<Map> datas = new ArrayList<>();
            List<Header> headerList = parseInfo.getHeaders();
            if((headerList == null || headerList.size() == 0) && parseInfo.getSheetHeaderMap().containsKey(ParseInfo.COMMONE_SHEET_NAME)){
                headerList = parseInfo.getSheetHeaderMap().get(ParseInfo.COMMONE_SHEET_NAME);
            }

            setFileName();

            parseInfo.setHeaders(headerList);

            int curRow = 1;
            Map map = null;

            StringBuffer headerNotFound = new StringBuffer();
            for (Iterator<CSVRecord> iter = records.iterator() ; iter.hasNext() ;){
                CSVRecord rec = iter.next();
                map = rec.toMap();
                //空行，不处理
                if(RegExpUtil.isEmpty(map)){
                    continue;
                }

                if (map.size() > 0 && !RegExpUtil.isEmpty(map)) {
                    Map<String,String> rowData = new HashMap();

                    int _count =  setRowValue(rowData,rec,headerList,headerNotFound);

                    //空行，不处理
                    if(RegExpUtil.isEmpty(rowData)){
                        continue;
                    }

                    //空行，不处理
                    if(_count == headerList.size()){
                        continue;
                    }
                    Map rs = new HashMap();
                    rs.put(CellDataHandle.ROW_NUMBER_NAME,curRow);
                    rs.putAll(rowData);



                    ExcelCommonHandle.cellHandle(curRow,rowData,rs,parseInfo,getExistList());

                    if(parseInfo.getRowDataHandles() != null){
                        ExcelCommonHandle.rowHandle(parseInfo.getRowDataHandles(),fileName,curRow,rs,rowData,parseInfo);
                    }

                    ExcelCommonHandle.sendParseResult(fileName,curRow,rs,parseInfo,onlyParse);

                    //发送行处理结果监听
                    ExcelCommonHandle.sendEndLineParseResult(parseInfo,fileName,curRow,rs);

                    if(!onlyParse){
                        datas.add(rs);
                    }

                }
                if (logger.isDebugEnabled()) {
                    logger.debug("sheetName->" + fileName + "\tcurRow->" + curRow + "\trow->" + map);
                }

                if(headerNotFound.length() > 0){
                    logger.warn("row["+curRow+"] not found headers:"+headerNotFound);
                    headerNotFound.delete(0,headerNotFound.length());
                }
                curRow++ ;
            }

            if(datas.size() == 0){
                logger.info("parser csv file is not found any data for["+fileName+"]");
            }

            if(!onlyParse){
                dataMap.put(fileName,datas);
            }

            datas = null;
        }catch (IOException e){
            throw e;
        }

    }

    private List<String> getExistList() {
        if(existList == null){
            existList = new ArrayList<>();
        }
        return existList;
    }

    private int setRowValue(Map<String,String> rowData, CSVRecord rec, List<Header> headerList, StringBuffer headerNotFound) {
        int _count = 0;
        for (Header h : headerList) {
            if(rec.isMapped(h.getExcelName())) {
                String value = rec.get(h.getExcelName());
                if (Utils.isEmpty(value)) {
                    _count++;
                }
                rowData.put(h.getName(), value);
            }else{
                headerNotFound.append(h.getExcelName()).append(",");
            }
        }
        return  _count;
    }

    private CSVParser getRecordIterator() throws IOException {
        CSVFormat csvFormat =  CSVFormat.newFormat(CsvConstants.getChar(parseInfo.getCsvSeperator()));
        if(parseInfo.isCsvFirstIsHeader()){
            csvFormat = csvFormat.withFirstRecordAsHeader();
        }else{
            String[] headers = getHeaders();
            csvFormat = csvFormat.withHeader(headers);
        }
        Reader in = new FileReader(parseInfo.getFile());
        return csvFormat.parse(in);

    }

    private void setFileName() {
        if(parseInfo.getSheets() != null && parseInfo.getSheets().size() > 0  && parseInfo.getSheets().get(0) != null){
            fileName = parseInfo.getSheets().get(0);
        }
        if(fileName == null) {
            fileName = parseInfo.getFile().getName();
            if (fileName.indexOf(".") > 0) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
        }
    }

    private String[] getHeaders() {
       List<Header> headerList = parseInfo.getHeaders();
       if(headerList == null || headerList.size() == 0){
           if(parseInfo.getSheetHeaderMap().containsKey(ParseInfo.COMMONE_SHEET_NAME)){
               parseInfo.setHeaders(parseInfo.getSheetHeaderMap().get(ParseInfo.COMMONE_SHEET_NAME));
           }
       }
        headerList = parseInfo.getHeaders();
        if(headerList == null || headerList.size() == 0){
           throw new ExcelParseException("csv头部数据不存在");
        }
        Collections.sort(headerList, new Comparator<Header>() {
            public int compare(Header o1, Header o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });

       List<Header> fullList = new LinkedList<>();
       int pre = 0;
       for(Header h : headerList){
           if(h.getIndex() == pre){
               fullList.add(h);
               pre++;
           }else if(h.getIndex() > pre){
               int step = h.getIndex() - pre;
               for(int i = 0 ; i < step ; i++){
                   Header _h = new Header("H"+(pre+i),"H"+(pre+i),(pre+i));
                   fullList.add(_h);
               }
               fullList.add(h);
               pre = h.getIndex() + 1;
           }else{
               //nothing todo
           }
       }

       String[] headers = new String[fullList.size()];
       pre = 0 ;
       for(Header header : fullList){
           headers[pre++] = header.getExcelName();
       }
       return headers;
    }

}
