package jk.core.excel.parse.base;

import jk.core.ex.ConvertDataException;
import jk.core.ex.ParseHeaderException;
import jk.core.util.RegExpUtil;
import jk.core.util.Utils;
import l.jk.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: ExcelUtil.java
 * @author: liguohui lgh1177@126.com
 * @version: v1.0
 */
public class ParserUtil {
	public static final Logger logger = LogManager.getLogger(ParserUtil.class);

	public static synchronized void parseHeader(String sheetName, ParseInfo info,
                                                List<String> list) {
		int len = 0;
		resetHeaderList(info,sheetName);
		if (info.isNoHeader()) {
			if (info.getHeaders() == null) {
				info.setHeaders(new ArrayList<Header>());
				len = list.size();
			} else if (list.size() > info.getHeaders().size()) {
				len = list.size() - info.getHeaders().size();
			}
			while (len > 0) {
				int size = info.getHeaders().size() + 1;
				Header h = new Header(String.valueOf(size), null, size);
				h.setMatcher(true);
				info.getHeaders().add(h);
				len--;
			}
		} else {
			if(info.getHeaders() == null || info.getHeaders().size() == 0){
				return;
			}
			for (String excelName : list) {
				if (excelName == null) {
					len++;
					continue;
				}
				excelName = clearName(excelName);
				for (Header h : info.getHeaders()) {
					if(isMappingHeader(excelName,h,info.isParseUnit())){
						h.setIndex(len);
						h.setMatcher(true);
						break;
					}
//					if (excelName.equals(h.getExcelName())) {
//
//					}
				}
				len++;
			}

		}

	}

	/**
	 * 判断头部，同时找出单位，并设置为header
	 * @param excelName
	 * @param header
	 * @param parseUnit
	 * @return
	 */
	private static boolean isMappingHeader(String excelName, Header header, boolean parseUnit) {
		if(excelName.equals(header.getExcelName())){
			return true;
		}
		if(header.getExcelName() == null || header.getExcelName().length() == 0){
			return false;
		}
		if(parseUnit){
			//去掉excel的单位的name
			HeaderUnit _excelUnit = getExcelName(excelName);
			if(_excelUnit.name == null || _excelUnit.name.length() == 0){
				return false;
			}
			//如果_excelName为空，则说明整个excel的头部名称只有单位，不做处理，当做异常数据
			if(_excelUnit.name.equals(header.getExcelName())){
				header.setUnit(_excelUnit.unit);
				return true;
			}

			//去掉header的单位与excel去掉单位的name进行比对
			HeaderUnit headerExcelUnit = getExcelName(header.getExcelName());
			if(headerExcelUnit.name == null || headerExcelUnit.name.length() == 0){
				return false;
			}
			//就近原则，如果header mapping上有单位且excel头有单位，则以excel头单位为准
			if(_excelUnit.name.equals(headerExcelUnit.name)){
				if(_excelUnit.unit != null && _excelUnit.unit.length() > 0){
					header.setUnit(_excelUnit.unit);
				}else {
					header.setUnit(headerExcelUnit.unit);
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * 如果有单位，则返回没有单位的列头名称，如果没有单位，则返回原始数据；
	 * 单位只接受以)或者）结尾的表头。
	 * 判断单位的逻辑是：1、必须满足单位的表头格式，2、必须是在系统预设的类中存在(Units)
	 * @param excelName
	 * @return
	 */
	private static HeaderUnit getExcelName(String excelName) {
		excelName = excelName.replaceAll("（+","(");
		excelName = excelName.replaceAll("）+",")");
		if(excelName.endsWith(")") && excelName.contains("(")){
			String _excelName = excelName.substring(0,excelName.lastIndexOf("("));
			String unit = excelName.substring(excelName.lastIndexOf("(")+1,excelName.length()-1);
			if(Units.contains(unit)){
				HeaderUnit hu = new HeaderUnit();
				hu.name = _excelName;
				hu.unit = unit;
				return hu;
			}
		}
		HeaderUnit hu = new HeaderUnit();
		hu.name = excelName;
		return hu;

	}

	private static String clearName(String excelName) {
		if(excelName == null){
			return excelName;
		}
		excelName = excelName.trim();
		excelName = excelName.replaceAll("\\n", "");
		return excelName;
	}

	private static void resetHeaderList(ParseInfo info, String sheetName) {
		sheetName = ParseUtils.isEmpty(sheetName) ? ParseInfo.COMMONE_SHEET_NAME : sheetName;
		sheetName = sheetName.trim();
		sheetName = sheetName.toLowerCase();
		if(info.getSheetHeaderMap().containsKey(sheetName)){
			info.setHeaders(info.getSheetHeaderMap().get(sheetName));
		}else if(info.getSheetHeaderMap().containsKey(ParseInfo.COMMONE_SHEET_NAME)){
			//兼容未指定sheet name的header信息
			info.setHeaders(info.getSheetHeaderMap().get(ParseInfo.COMMONE_SHEET_NAME));
		} else{
			//清空不解析的头部信息
			info.setHeaders(null);
		}
	}

	/**
	 * @param info
	 */
	public static void checkHeaderInfos(ParseInfo info, String sheenName) {
		if (info.isForceMatcher()) {
			List<Header> headers = info.getHeaders();
			if(headers == null || headers.size() == 0){
				throw new ParseHeaderException("表头信息未配置或者表头为空-->sheetName(" + sheenName + ")");
			}
			StringBuffer buffer = new StringBuffer();
			for (Header h : headers) {
				if (h != null && !h.isMatcher()) {
					buffer.append(",").append(h.getExcelName());
				}
			}
			if (buffer.length() > 0) {
				throw new ParseHeaderException("以下表头未正确匹配-->("
						+ buffer.substring(1) + ")");
			}
		}

	}

	public static Map<Integer, Header> toMap(List<Header> list) {
		if (list != null) {
			Map<Integer, Header> map = new HashMap<Integer, Header>();
			for (Header h : list) {
				if (h != null && h.isMatcher()) {
					map.put(h.getIndex(), h);
				}
			}
			return map;
		}
		return null;
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param data
	 * @param c
	 * @return
	 */
	public static <T> List<T> convertData(List<Map> data, Class<T> c) {
		if (data == null) {
			return null;
		}
		try {
			List<T> list = new ArrayList<T>();
			List<Property> pros = getProperties(c);
			Map<String, Property> proMap = ParseUtils.listToMap(pros, "name");

			for (Map map : data) {
				if (map != null) {
//					T t = c.newInstance();
//					for (Iterator<String> iter = map.keySet().iterator(); iter
//							.hasNext();) {
//						String key = iter.next();
//						Object val = map.get(key);
//						setValue(t, proMap.get(key), val);
//					}
					T t = JSONObject.toJavaObject(map,c);
					list.add(t);
				}
			}

			return list;
		} catch (Exception e) {
			throw new ConvertDataException(e.getMessage(), e);
		}

	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param property
	 * @param val
	 */
	private static void setValue(Object obj, Property property, Object val) {
		if (property == null) {
			return;
		}
		try {
			Method m = obj.getClass().getMethod(property.getWriteMethod(),
					property.getType());
			m.invoke(obj, getValueByType(property.getType(), val));
		} catch (Exception e) {
			if (logger.isDebugEnabled())
				logger.info(e.getLocalizedMessage() + "---->"
						+ property.getName() + "[" + property.getType() + "]"
						+ "-->value=" + val);
		}
	}

	private static Object getValueByType(Class<?> type, Object v) {
		if (v == null) {
			return null;
		}
		if (!(v instanceof String)) {
			return v;
		}
		String value = String.valueOf(v);
		if (type.getName().equals(Boolean.class.getName())
				|| type.getName().equals(boolean.class.getName())) {
			boolean f = value == null || "".equals(value)
					|| "false".equals(value.toLowerCase()) || "1".equals(value) ? false
					: true;
			return f;
		} else if (type.getName().equals(String.class.getName())) {
			return value;
		} else if (type.getName().equals(Integer.class.getName())
				|| type.getName().equals(int.class.getName())) {
			value = RegExpUtil.removeSpecialSymbol(value);
			if (RegExpUtil.isInt(value)) {
				return Integer.parseInt(value);
			}
			return 0;
		} else if (type.getName().equals(Float.class.getName())
				|| type.getName().equals(float.class.getName())) {
			value = RegExpUtil.removeSpecialSymbol(value);
			if (RegExpUtil.isFloat(value)) {
				return Float.parseFloat(value);
			}
			return 0;
		} else if (type.getName().equals(Long.class.getName())
				|| type.getName().equals(long.class.getName())) {
			value = RegExpUtil.removeSpecialSymbol(value);
			if (RegExpUtil.isLong(value)) {
				return Long.parseLong(value);
			}
			return 0;
		} else if (type.getName().equals(Double.class.getName())
				|| type.getName().equals(double.class.getName())) {
			value = RegExpUtil.removeSpecialSymbol(value);
			if (RegExpUtil.isFloat(value)) {
				return Double.parseDouble(value);
			}
			return 0;
		} else if (type.getName().equals(Short.class.getName())
				|| type.getName().equals(short.class.getName())) {
			value = RegExpUtil.removeSpecialSymbol(value);
			if (RegExpUtil.isShort(value)) {
				return Short.parseShort(value);
			}
			return (short) 0;
		} else if (type.getName().equals(Date.class.getName())) {
			if (RegExpUtil.isDate(value)) {
				Date date = parseDate(value);
				return date;
			}
			return value;
		} else if (type.getName().equals(java.sql.Date.class.getName())) {
			if (RegExpUtil.isDate(value)) {
				Date date = parseDate(value);
				java.sql.Date sqlDate = new java.sql.Date(date.getTime());
				return sqlDate;
			}
			return value;
		} else if (type.getName().equals(BigDecimal.class.getName())) {
			value = RegExpUtil.removeSpecialSymbol(value);
			if (RegExpUtil.isNumber(value)) {
				return new BigDecimal(value);
			}
			return value;
		}
		return null;

	}

	/**
	 * <p>
	 * Discription:转换字符串为日期对象
	 * </p>
	 * 
	 * @param dateStr
	 *            日期字符串
	 *  支持
	 *            yyyy-MM-dd HH:mm:ss, yyyy-MM-dd HH:mm, yyyy-MM-dd 格式字符串时间
	 * @return
	 */
	public static Date parseDate(String dateStr) {
		String[] formats = new String[] { "yyyy-MM-dd HH:mm:ss",
				"yyyy-MM-dd HH:mm", "yyyy-MM-dd" };
		if (!Utils.isEmpty(dateStr)) {
			Date date = null;
			for (String s : formats) {
				try {
					SimpleDateFormat format = new SimpleDateFormat(s);
					date = format.parse(dateStr);
				} catch (Exception e) {

				}
				if (date != null) {
					return date;
				}
			}

			return null;
		}
		return null;
	}
	
	public static String formatDate(Date date){
		String[] formats = new String[] { "yyyy-MM-dd HH:mm:ss",
				"yyyy-MM-dd HH:mm", "yyyy-MM-dd" };
		if (!Utils.isEmpty(date)) {
			String rs = null;
			for (String s : formats) {
				try {
					SimpleDateFormat format = new SimpleDateFormat(s);
					rs = format.format(date);
				} catch (Exception e) {

				}
				if (rs != null) {
					return rs;
				}
			}

			return null;
		}
		return null;
	}

	public static List<Property> getProperties(Class c) {
		List<Property> pros = new ArrayList<Property>();
		find(pros, c);
		return pros;
	}

	/**
	 * <p>
	 * Discription:[方法功能中文描述]
	 * </p>
	 * 
	 * @param pros
	 * @param c
	 */
	private static void find(List<Property> pros, Class c) {
		Field[] fields = c.getDeclaredFields();
		for (Field f : fields) {
			if (Modifier.isFinal(f.getModifiers())
					|| Modifier.isStatic(f.getModifiers())
					|| "fields".equals(f.getName())) {
				continue;
			}
			Property p = new Property();
			p.setName(f.getName());
			p.setType(f.getType());
			String read = "";
			String write = "";
			if (f.getType() == Boolean.class || f.getType() == boolean.class) {
				write = "is" + f.getName().substring(0, 1).toUpperCase()
						+ f.getName().substring(1);
			} else {
				write = "set" + f.getName().substring(0, 1).toUpperCase()
						+ f.getName().substring(1);
			}
			read = "get" + f.getName().substring(0, 1).toUpperCase()
					+ f.getName().substring(1);
			p.setReadMethod(read);
			p.setWriteMethod(write);
			pros.add(p);
		}
		if (c.getSuperclass() != Object.class) {
			find(pros, c.getSuperclass());
		}
	}

	public static void main(String[] args) {
		System.out.println("123 \n 1456 \n789".replaceAll("\\n", ""));
	}
}


class HeaderUnit{
	public String name;
	public String unit;
}