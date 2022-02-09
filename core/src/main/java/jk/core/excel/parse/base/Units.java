package jk.core.excel.parse.base;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author liguohui  lgh1177@126.com
 */
public final class Units {
    public final static Set<String> UNITS = Collections.synchronizedSet(new HashSet<String>());

    static {
        UNITS.add("元");
        UNITS.add("万元");
        UNITS.add("亿元");
        UNITS.add("米");
        UNITS.add("m");
        UNITS.add("千米");
        UNITS.add("km");
        UNITS.add("里");
        UNITS.add("公里");
        UNITS.add("斤");
        UNITS.add("公斤");
        UNITS.add("千克");
        UNITS.add("克");
        UNITS.add("g");
        UNITS.add("kg");
    }

    public static boolean contains(String unit){
        if(unit == null || unit.trim().length() == 0){
            return false;
        }
        return UNITS.contains(unit);
    }

}
