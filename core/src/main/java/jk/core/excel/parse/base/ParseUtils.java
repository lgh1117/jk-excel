package jk.core.excel.parse.base;

import com.alibaba.fastjson.JSONObject;
import jk.core.ex.ExcelParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParseUtils {
	private static final Log log = LogFactory.getLog(ParseUtils.class);

	/**
	 * <p>
	 * Discription:判断提供的对象是否为null、空字符“”、“null”、或者JSonNull对象
	 * </p>
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj) {
		return Objects.isNull(obj) || "".equals(obj.toString());
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param dbExist
	 * @param name
	 */
	public static Map listToMap(List dbExist, String name) {
		Map map = new HashMap();
		if (dbExist == null || dbExist.size() == 0) {
			return map;
		}
		for (Object obj : dbExist) {
			if (obj != null) {
				if (obj instanceof Map) {
					Map m = (Map) obj;
					map.put(m.get(name), obj);
				} else {
					String val = getValue(obj, name);
					map.put(val, obj);
				}
			}
		}
		return map;
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static String getValue(Object obj, String fieldName) {
		try {
			if (obj instanceof Map) {
				Object val = ((Map) obj).get(fieldName);
				return val == null ? null : val.toString();
			}
			fieldName = fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			Method m = obj.getClass().getDeclaredMethod("get" + fieldName);
			Object val = m.invoke(obj);
			return val == null ? null : val.toString();
		} catch (Throwable e) {
			log.debug(e.getMessage());
			return null;
		}
	}

	public static Map<String, Object> beanToMap(Object bean) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (bean == null) {
			return map;
		}
		Object obj = JSONObject.toJSON(bean);
		if(obj instanceof JSONObject){
			return (JSONObject)obj;
		}
		throw new ExcelParseException("错误转化");
	}
}
