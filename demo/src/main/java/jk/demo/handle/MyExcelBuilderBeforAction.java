package jk.demo.handle;


import jk.core.builder.ParseBeforeAction;
import jk.core.builder.ParseBuilder;
import jk.core.excel.parse.base.ParseInfo;

/**
 * @ClassName MyExcelBuilderBeforAction
 * @Description
 * @Version 1.0.0
 * @Author liguohui
 * @Since 2021/10/21 下午5:04
 */
public class MyExcelBuilderBeforAction implements ParseBeforeAction {
    @Override
    public void exec(ParseBuilder builder) {
        ParseInfo parseInfo = builder.getParseInfo();
        if(parseInfo.getMappings() != null){
            parseInfo.getMappings().forEach( m -> {
                if(m.getExcelName().equals("生日")){
                    m.addHandle(new MyCellDataHandle());
                }
            });
        }
    }
}
