/**
 *
 */
package jk.core.excel.cellinfo;

import jk.core.excel.parse.base.ParseUtils;
import jk.core.ex.ExcelParseException;

import java.util.Arrays;

/**
 * 增强导出的excel的颜色，字体等设置
 *
 * @author Jack lee
 */
public class CellInfo {

    /**
     * 加粗权重，false为BOLDWEIGHT_NORMAL,true为BOLDWEIGHT_BOLD
     */
    private boolean boldWeight = false;

    /**
     * 设置字体大小
     */
    private short fontHightInPoints = -1;

    /**
     * 设置字体颜色;其中color，rgbColor，hexColor都代表颜色，优先级是color > rgbColor > hexColor；
     * 引用
     *
     * @see Colors
     */
    private short color = -1;

    /**
     * 设置字符编码
     *
     * @see Charsets
     */
    private int charset = -1;

    /**
     * 是否加粗，true加粗，默认不加粗
     */
    private boolean bold = false;

    /**
     * 背景填充色
     */
    private short fillColor = -1;

    /**
     * 自动换行
     */
    private boolean wrapText = false;

    /**
     * 设置字体颜色，rgb 三色设置,如:r:111,g:222,b:123
     * ；;其中color，rgbColor，hexColor都代表颜色，优先级是color > rgbColor > hexColor
     */
    private byte[] rgbColor;

    /**
     * 设置背景颜色，rgb 三色设置,如:r:111,g:222,b:123
     * ；;其中color，rgbColor，hexColor都代表颜色，优先级是color > rgbColor > hexColor
     */
    private byte[] fillRgbColor;


    /**
     * 十六进制设置字体颜色，必须6位16进制数，如：CCCCCC
     * ；;其中color，rgbColor，hexColor都代表颜色，优先级是color > rgbColor > hexColor
     */
    private String hexColor;

    /**
     * 十六进制设置背景颜色，必须6位16进制数，如：CCCCCC
     * ；;其中color，rgbColor，hexColor都代表颜色，优先级是color > rgbColor > hexColor
     */
    private String fillHexColor;

    /**
     * 单元格字体
     */
    private String fontName;

    /**
     * 单元格格式样式，如下：
     *
     * 0, "General"
     * 1, "0"
     * 2, "0.00"
     * 3, "#,##0"
     * 4, "#,##0.00"
     * 5, "($#,##0_);($#,##0)"
     * 6, "($#,##0_);[Red]($#,##0)"
     * 7, "($#,##0.00);($#,##0.00)"
     * 8, "($#,##0.00_);[Red]($#,##0.00)"
     * 9, "0%"
     * 0xa, "0.00%"
     * 0xb, "0.00E+00"
     * 0xc, "# ?/?"
     * 0xd, "# ??/??"
     * 0xe, "m/d/yy"
     * 0xf, "d-mmm-yy"
     * 0x10, "d-mmm"
     * 0x11, "mmm-yy"
     * 0x12, "h:mm AM/PM"
     * 0x13, "h:mm:ss AM/PM"
     * 0x14, "h:mm"
     * 0x15, "h:mm:ss"
     * 0x16, "m/d/yy h:mm"
     * 0x17 - *0x24 reserved for international and undocumented *0x25, "(#,##0_);(#,##0)"
     * 0x26, "(#,##0_);[Red](#,##0)"
     * 0x27, "(#,##0.00_);(#,##0.00)"
     * 0x28, "(#,##0.00_);[Red](#,##0.00)"
     * 0x29, "_(*#,##0_);_(*(#,##0);_(* \"-\"_);_(@_)"
     * 0x2a, "_($*#,##0_);_($*(#,##0);_($* \"-\"_);_(@_)"
     * 0x2b, "_(*#,##0.00_);_(*(#,##0.00);_(*\"-\"??_);_(@_)"
     * 0x2c, "_($*#,##0.00_);_($*(#,##0.00);_($*\"-\"??_);_(@_)"
     * 0x2d, "mm:ss"
     * 0x2e, "[h]:mm:ss"
     * 0x2f, "mm:ss.0"
     * 0x30, "##0.0E+0"
     * 0x31, "@" - This is text format.
     * 0x31 "text" - Alias for "@"
     */
    private String dataFormat;

    /**
     * 数据格式样式序列码，与dataFormat一致，如果dataFormat不为空，则由dataFormat来进行转换
     */
    private Short dataFormatIndex;


    /**
     * 加粗权重，false为BOLDWEIGHT_NORMAL,true为BOLDWEIGHT_BOLD
     */
    public boolean isBoldWeight() {
        return boldWeight;
    }

    /**
     * 加粗权重，false为BOLDWEIGHT_NORMAL,true为BOLDWEIGHT_BOLD
     */
    public void setBoldWeight(boolean boldWeight) {
        this.boldWeight = boldWeight;
    }

    /**
     * 设置字体高度
     */
    public short getFontHightInPoints() {
        return fontHightInPoints;
    }

    /**
     * 设置字体高度
     */
    public void setFontHightInPoints(short fontHightInPoints) {
        this.fontHightInPoints = fontHightInPoints;
    }

    /**
     * 设置颜色
     */
    public short getColor() {
        return color;
    }

    /**
     * 设置颜色
     */
    public void setColor(short color) {
        this.color = color;
    }

    /**
     * 设置颜色
     */
    public void setColor(Colors color) {
        this.color = color.getIndex();
    }

    /**
     * 设置字符编码
     */
    public int getCharset() {
        return charset;
    }

    /**
     * 设置字符编码
     */
    public void setCharset(int charset) {
        this.charset = charset;
    }

    /**
     * 设置字符编码
     */
    public void setCharset(Charsets charset) {
        this.charset = charset.getValue();
    }

    /**
     * 是否加粗，true加粗，默认不加粗
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * 是否加粗，true加粗，默认不加粗
     */
    public void setBold(boolean bold) {
        this.bold = bold;
    }

    /**
     * 背景填充色
     */
    public short getFillColor() {
        return fillColor;
    }

    /**
     * 背景填充色
     */
    public void setFillColor(short fillColor) {
        this.fillColor = fillColor;
    }

    public boolean isWrapText() {
        return wrapText;
    }

    public byte[] getRgbColor() {
        return rgbColor;
    }

    public void setRgbColor(byte[] rgbColor) {
        this.rgbColor = rgbColor;

    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        setRgbColor(hexToByte(hexColor));
    }

    private byte[] hexToByte(String hexColor) {
        hexColor = hexColor == null ? "" : hexColor.trim();
        if (ParseUtils.isEmpty(hexColor)) {
            throw new ExcelParseException("hexColor must not null");
        }
        if (hexColor.length() != 6) {
            throw new ExcelParseException("the length of hexColor must be 6 character ");
        }
        for (char c : hexColor.toCharArray()) {
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                continue;
            } else {
                throw new ExcelParseException("hexColor must be 0 ~ 9 or a ~ f or A ~ F ");
            }

        }
        this.hexColor = hexColor;
        byte r = (byte) Integer.parseInt(hexColor.substring(0, 2), 16);
        byte g = (byte) Integer.parseInt(hexColor.substring(2, 4), 16);
        byte b = (byte) Integer.parseInt(hexColor.substring(4, 6), 16);
        return new byte[]{r, g, b};
    }

    public byte[] getFillRgbColor() {
        return fillRgbColor;
    }

    public void setFillRgbColor(byte[] fillRgbColor) {
        this.fillRgbColor = fillRgbColor;
    }

    public String getFillHexColor() {
        return fillHexColor;
    }

    public void setFillHexColor(String fillHexColor) {
        this.fillHexColor = fillHexColor;
        setFillRgbColor(hexToByte(fillHexColor));
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }


    public Short getDataFormatIndex() {
        return dataFormatIndex;
    }

    public void setDataFormatIndex(Short dataFormatIndex) {
        this.dataFormatIndex = dataFormatIndex;
    }

    @Override
    public String toString() {
        return "CellInfo{" +
                "boldWeight=" + boldWeight +
                ", fontHightInPoints=" + fontHightInPoints +
                ", color=" + color +
                ", charset=" + charset +
                ", bold=" + bold +
                ", fillColor=" + fillColor +
                ", wrapText=" + wrapText +
                ", rgbColor=" + Arrays.toString(rgbColor) +
                ", fillRgbColor=" + Arrays.toString(fillRgbColor) +
                ", hexColor='" + hexColor + '\'' +
                ", fillHexColor='" + fillHexColor + '\'' +
                ", fontName='" + fontName + '\'' +
                ", dataFormat='" + dataFormat + '\'' +
                ", dataFormatIndex=" + dataFormatIndex +
                '}';
    }
}
