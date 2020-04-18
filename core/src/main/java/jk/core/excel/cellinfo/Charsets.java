/**
 * 
 */
package jk.core.excel.cellinfo;

/**
 * @author Jack lee
 *
 */
public enum Charsets {
     ANSI(0),
     DEFAULT(1),
     SYMBOL(2),
     MAC(77),
     SHIFTJIS(128),
     HANGEUL(129),
     JOHAB(130),
     GB2312(134),
     CHINESEBIG5(136),
     GREEK(161),
     TURKISH(162),
     VIETNAMESE(163),
     HEBREW(177),
     ARABIC(178),
     BALTIC(186),
     RUSSIAN(204),
     THAI(222),
     EASTEUROPE(238),
     OEM(255);

    
    private int charset;

    private Charsets(int value){
        charset = value;
    }

    /**
     * Returns value of this charset
     *
     * @return value of this charset
     */
    public int getValue(){
        return charset;
    }

}