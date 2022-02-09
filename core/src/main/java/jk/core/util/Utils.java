package jk.core.util;

import java.util.Collection;
import java.util.Objects;

/**
 * @ClassName Utils
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 * @Since 2020/4/16 上午12:38
 */
public class Utils {
    public static boolean isEmpty(Object obj) {
        return Objects.isNull(obj) || "".equals(obj);
    }

    public static boolean isEmptyCollection(Collection collection){
        return collection == null || collection.isEmpty();
    }
}
