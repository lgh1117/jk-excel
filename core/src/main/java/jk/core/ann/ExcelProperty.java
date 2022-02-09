package jk.core.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 生成和解析excel时，配合实例类来使用，减少配置
 * @ClassName ExcelProperty
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 * @Since 2022/2/6 下午1:02
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelProperty {
    /**
     * 生成、解析均需要此属性
     * excel的名称，在excel文件上的，自动去掉空格、换行符号等
     * 必须属性
     * @return
     */
    public String name();

    /**
     * 生成、解析时均需要此属性
     * 数据属性名称，如Person这个对象的name属性，要来寻找数据使用；默认为Bean的property名字
     * @return
     */
    public String property() default "";

    /**
     * 该名称占用几列,
     * 生成时需要，默认为1
     * 生成时使用
     */
    public int col() default 1;

    /**
     * 该名称占用几行
     * 生成时需要，默认为1
     * 生成时使用
     */
    public int row() default 1;

    /**
     * 列头在第几行,默认为第1行
     * 生成时使用
     */
    public int rowIndex() default 1;

    /***
     * 所处列的位置,从1开始，默认按照属性所处类的位置排序，一次增加所占行
     * 生成时使用
     */
    public int colIndex() default -1;

    /**
     * 列宽,即每个单元格的宽度，可以不用设置
     * 生成时使用
     */
    public int  colWidth() default -1;

    /**
     * 自动按照列头的宽度调整列宽
     * 生成时使用
     */
    public boolean autoWidth() default false;

    /**
     * csv文件的时候，可以用来做定位使用
     * @return
     */
    int index() default 0;

    /**
     * 解析时，单元格的处理器，可以为空
     * @return
     */
    public Class[] cellDataHandleClass() default {};

}
