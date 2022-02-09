package jk.core.builder;

import jk.core.Excel;
import jk.core.ParseFactory;
import jk.core.ann.ExcelProperty;
import jk.core.ex.ExcelParseException;
import jk.core.excel.parse.base.Mapping;
import jk.core.excel.parse.base.ParseInfo;
import jk.core.hd.CellDataHandle;
import jk.core.hd.RowDataHandle;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ParseBuilder
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 * @Since 2022/2/6 下午1:19
 */
public final class ParseBuilder {

    /**
     * 解析时的配置类
     */
    private ParseInfo parseInfo = new ParseInfo();
    /**
     * 在执行解析前回调，让使用者对ParseInfo参数可以进行调整
     */
    private ParseBeforeAction action;

    private ParseBuilder(){}

    /**
     * 解析excel或生成excel接收类
     */
    private Class t;

    /**
     * 创建自己的一个实例
     * @return
     */
    public static <T> ParseBuilder create(Class<T> cls){
        ParseBuilder builder = new ParseBuilder();
        builder.t = cls;
        return builder;
    }

    public static ParseBuilder create(){
        return new ParseBuilder();
    }

    public ParseInfo getParseInfo(){
        return this.parseInfo;
    }
    public void setParseInfo(ParseInfo parseInfo){
        this.parseInfo = parseInfo;
    }


    private Class getGenericClass(){
        Type type = getClass().getGenericSuperclass();
        if(type == null){
            return null;
        }
        Type[] types = ((ParameterizedType)type).getActualTypeArguments();
        if(types == null || types.length == 0){
            return null;
        }
        return (Class)types[0];
    }

    /**
     * 如果不设置，则通过反射获取，如果设置，以此为准
     * @param mappings
     * @return
     */
    public ParseBuilder mapping(List<Mapping> mappings){
        parseInfo.setMappings(mappings);
        return this;
    }

    /**
     * 设置数据从第几列开始，默认为1
     * @param index
     * @return
     */
    public ParseBuilder dataIndex(int index){
        parseInfo.setDataIndex(index);
        return this;
    }

    /**
     * 需要解析的文件
     * @param file
     * @return
     */
    public ParseBuilder dataFile(File file){
        parseInfo.setFile(file);
        return this;
    }

    /**
     * 是否强制列头必须全部匹配，在解析的时候,默认否
     * @param forceMath
     * @return
     */
    public ParseBuilder forceMatch(Boolean forceMath){
        parseInfo.setForceMatcher(forceMath);
        return this;
    }

    /**
     * 设置解析是映射的文件规则
     * @param file
     * @return
     */
    public ParseBuilder mappingFile(File file){
        parseInfo.setMappingFile(file);
        return this;
    }

    /**
     * 指定解析哪个sheet名称
     * @param sheetName
     * @return
     */
    public ParseBuilder sheetName(String sheetName){
        parseInfo.addSheet(sheetName);
        return this;
    }


    public ParseBuilder csvSeperator(String sep){
        parseInfo.setCsvSeperator(sep);
        return this;
    }

    public ParseBuilder csvFormat(CSVFormat csvFormat){
        parseInfo.setCsvFormat(csvFormat);
        return this;
    }

    /**
     * 设置字符集，当前只有解析csv时有效
     * @param name
     * @return
     */
    public ParseBuilder charset(String name){
        parseInfo.setCharset(name);
        return this;
    }

    /**
     * 设置根据文件自动测探文件编码,会带来性能问题，优先级小于charset方法
     * @return
     */
    public ParseBuilder autoDetectorCharset(){
        parseInfo.setAutoDetectorCharset(true);
        return this;
    }

    /**
     * 设置在开始解析前，parseinfo初始化后，调用此回调
     * @param action
     * @return
     */
    public ParseBuilder beforeParse(ParseBeforeAction action){
        this.action = action;
        return this;
    }

    public ParseBuilder rowDataHandle(RowDataHandle handle){
        this.parseInfo.setRowDataHandle(handle);
        return this;
    }

    /**
     * 只发起解析，没有任何数据返回
     * @return
     */
    public void parse(){
        buildParse();
        Excel excel = ParseFactory.getExcelParse(parseInfo);
        excel.parse();
    }

    /**
     * 解析数据，以map集合形式返回
     * @return
     */
    public List<Map> parseToMapList(){
        buildParse();
        Excel excel = ParseFactory.getExcelParse(parseInfo);
        return excel.parseToMapList();
    }

    /**
     * 解析所有的数据返回
     * @return
     */
    public Map<String, List<Map>> parseAll(){
        buildParse();
        Excel excel = ParseFactory.getExcelParse(parseInfo);
        return excel.parseAllSheetToMapList();
    }

    /**
     * 解析数据返回Bean
     * @return
     */
    public List parseToBean(){
        buildParse();
        Excel excel = ParseFactory.getExcelParse(parseInfo);
        return excel.parseToList(this.t);
    }

    private void buildParse() {
        buildMapping();
        if(parseInfo.getFile() == null ){
            throw new ExcelParseException("找不到需要解析的文件");
        }

        if (!parseInfo.getFile().exists() || !parseInfo.getFile().canRead()){
            throw new ExcelParseException("无效的数据文件：" + parseInfo.getFile());
        }
        if(parseInfo.getDataIndex() < 0){
            throw new ExcelParseException("数据行号必须从0开始");
        }
        if(this.action != null){
            this.action.exec(this);
        }
    }

    private void buildMapping() {
        if(parseInfo.getMappings() != null && parseInfo.getMappings().size() > 0){
            return;//手动设置的mapping优先级最高
        }
        if(parseInfo.getMappingFile() != null){
            if(!parseInfo.getMappingFile().exists()){
                throw new ExcelParseException("需要解析的文件不存在：" + parseInfo.getMappingFile());
            }
            if(!parseInfo.getMappingFile().canRead()){
                throw new ExcelParseException("需要解析的文件不能读取：" + parseInfo.getMappingFile());
            }
            return;//有影射文件的，优先级第二
        }
        if(t == null){
            throw new ExcelParseException("请设置解析excel的映射信息");
        }
        List<Mapping> mappingList = parseMapping(t);
        if(mappingList == null || mappingList.size() == 0){
            throw new ExcelParseException("请设置解析excel的映射信息");
        }
        parseInfo.setMappings(mappingList);
    }

    private List<Mapping> parseMapping(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        if(fields == null || fields.length == 0){
            return null;
        }
        List<Mapping> mappingList = new ArrayList<>();
        for(Field field : fields){
            ExcelProperty ann = field.getAnnotation(ExcelProperty.class);
            if(ann == null){
                continue;
            }
            String excelName = ann.name();
            if(StringUtils.isEmpty(excelName)){
                throw new ExcelParseException("类：" + cls + "对应的字段：" + field.getName() + " 未设置名称，与excel的列头一致");
            }
            String property = StringUtils.isEmpty(ann.property()) ? field.getName() : ann.property();
            Mapping m = new Mapping(property,excelName);
            setHandles(m,ann);
            mappingList.add(m);
        }
        return mappingList;
    }

    private void setHandles(Mapping m, ExcelProperty ann) {
        Class[] handleCls = ann.cellDataHandleClass();
        if(handleCls != null && handleCls.length > 0){
            for(Class c : handleCls){
                try{
                    m.addHandle((CellDataHandle) c.newInstance());
                }catch (Throwable ex){
                    new RuntimeException(ex.getMessage(),ex);
                }
            }
        }
    }


}
