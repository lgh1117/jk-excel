package l.jk.json;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @ClassName JSONUtils
 * @Description
 * @Version 1.0.0
 * @Author liguohui
 * @Since 2020/6/2 下午2:46
 */
public class JSONUtils {
    static final int MAX_NESTING_DEPTH = 100;
    static final int MAX_STRING_LENGTH = 1024 * 1024;
    static final int MAX_NUMBER_LENGTH = 1000;
    static final int MAX_NAME_LENGTH = 10000;
    static final int MAX_ARRAY_GAP = 1024;
    static final int EXCEPTION_PREVIEW_LENGTH = 64;

    private static final Log log = LogFactory.getLog(JSONUtils.class);

    // 加载速度太慢了，放在静态代码块中
    private static ObjectMapper mapper;
    /** 为 true 时，异常信息附带输入的前 64 个字符。默认关闭。 */
    private static volatile boolean includeInputInException;

    /**
     * 设置一些通用的属性
     */
    static {
        mapper = new ObjectMapper();
        mapper.getFactory().setStreamReadConstraints(StreamReadConstraints.builder()
                .maxNestingDepth(MAX_NESTING_DEPTH)
                .maxStringLength(MAX_STRING_LENGTH)
                .maxNumberLength(MAX_NUMBER_LENGTH)
                .maxNameLength(MAX_NAME_LENGTH)
                .build());
        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        // 如果存在未知属性，则忽略不报错
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void setIncludeInputInException(boolean include) {
        includeInputInException = include;
    }

    public static String toJSONString(Object obj) {
        return obj != null ? toJSONString(obj, () -> "") : "";
    }

    public static String toJSONString(Object obj, Supplier<String> defaultSupplier) {
        try {
            return obj != null ? mapper.writeValueAsString(obj) : defaultSupplier.get();
        } catch (Throwable e) {
            throw new JSONException("toJSONString failed" + detail(null, obj), e);
        }
    }

    public static <T> T toJavaObject(String value, Class<T> tClass) {
        return isNotBlank(value) ? toJavaObject(value, tClass, () -> null) : null;
    }

    public static <T> T toJavaObject(Object obj, Class<T> tClass) {
        return obj != null ? toJavaObject(toJSONString(obj), tClass, () -> null) : null;
    }

    public static <T> T toJavaObject(String value, Class<T> tClass, Supplier<T> defaultSupplier) {
        try {
            if (isBlank(value)) {
                return defaultSupplier.get();
            }
            return mapper.readValue(value, tClass);
        } catch (Throwable e) {
            throw new JSONException("toJavaObject failed" + detail(tClass, value), e);
        }
    }

    private static boolean isBlank(String value){
        return value == null || value.length() == 0;
    }

    public static <T> List<T> toJavaObjectList(String value, Class<T> tClass) {
        return isNotBlank(value) ? toJavaObjectList(value, tClass, () -> null) : null;
    }

    public static <T> List<T> toJavaObjectList(Object obj, Class<T> tClass) {
        return obj != null ? toJavaObjectList(toJSONString(obj), tClass, () -> null) : null;
    }

    public static <T> List<T> toJavaObjectList(String value, Class<T> tClass, Supplier<List<T>> defaultSupplier) {
        try {
            if (isBlank(value)) {
                return defaultSupplier.get();
            }
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, tClass);
            return mapper.readValue(value, javaType);
        } catch (Throwable e) {
            throw new JSONException("toJavaObjectList failed" + detail(tClass, value), e);
        }
    }

    // 简单地直接用json复制或者转换(Cloneable)
    public static <T> T jsonCopy(Object obj, Class<T> tClass) {
        return obj != null ? toJavaObject(toJSONString(obj), tClass) : null;
    }

    public static Map<String, Object> toMap(String value) {
        return isNotBlank(value) ? toMap(value, () -> null) : null;
    }

    public static Map<String, Object> toMap(Object value) {
        return value != null ? toMap(value, () -> null) : null;
    }

    public static Map<String, Object> toMap(Object value, Supplier<Map<String, Object>> defaultSupplier) {
        if (value == null) {
            return defaultSupplier.get();
        }
        return toMap(toJSONString(value), defaultSupplier);
    }

    public static Map<String, Object> toMap(String value, Supplier<Map<String, Object>> defaultSupplier) {
        if (isBlank(value)) {
            return defaultSupplier.get();
        }
        try {
            return toJavaObject(value, LinkedHashMap.class);
        } catch (Exception e) {
            throw new JSONException("toMap failed" + detail(null, value), e);
        }
    }


    public static List<Object> toList(String value) {
        return isNotBlank(value) ? toList(value, () -> null) : null;
    }

    public static List<Object> toList(Object value) {
        return value != null ? toList(value, () -> null) : null;
    }

    private static boolean isNotBlank(String value){
        return !isBlank(value);
    }

    public static List<Object> toList(String value, Supplier<List<Object>> defaultSuppler) {
        if (value == null || value.length() == 0) {
            return defaultSuppler.get();
        }
        try {
            return toJavaObject(value, List.class);
        } catch (Exception e) {
            throw new JSONException("toList failed" + detail(null, value), e);
        }
    }

    public static List<Object> toList(Object value, Supplier<List<Object>> defaultSuppler) {
        if (value == null) {
            return defaultSuppler.get();
        }
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return toList(toJSONString(value), defaultSuppler);
    }

    static String detail(Object target, Object input) {
        StringBuilder message = new StringBuilder();
        if (target instanceof Class) {
            message.append(", target=").append(((Class<?>) target).getName());
        } else if (target != null) {
            message.append(", target=").append(target);
        }
        if (input instanceof CharSequence) {
            message.append(", length=").append(((CharSequence) input).length());
        } else if (input instanceof byte[]) {
            message.append(", length=").append(((byte[]) input).length);
        } else if (input != null) {
            message.append(", type=").append(input.getClass().getName());
        }
        if (includeInputInException) {
            String preview = preview(input);
            if (preview != null) {
                message.append(", value=").append(preview);
            }
        }
        return message.toString();
    }

    private static String preview(Object input) {
        if (!(input instanceof CharSequence || input instanceof Number
                || input instanceof Boolean || input instanceof Character)) {
            return null;
        }
        String text = input.toString().replace('\r', ' ').replace('\n', ' ');
        if (text.length() > EXCEPTION_PREVIEW_LENGTH) {
            return text.substring(0, EXCEPTION_PREVIEW_LENGTH);
        }
        return text;
    }

}
