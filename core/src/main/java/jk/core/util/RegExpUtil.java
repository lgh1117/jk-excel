package jk.core.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***********************************************************************************************************************
 * 提供对常用数据的合法性验证，目前能验证的有： email： isEmail 身份证： isIdentity 邮编： isZip 整数：isInt
 * 浮点数：isFloat 数字：isNumber 日期：isDate 手机号码：isMobile 空或者null：isNull
 * 
 * @author liguohui lgh1177@126.com
 */
public class RegExpUtil {

	// 整数
	private static final String REG_INT = "^[0-9]+$";

	// 正整数
	private static final String POSITIVE_REG_INT = "^(\\+)?[0-9]+$";

	// 负整数
	private static final String NEGATIVE_REG_INT = "^(-){1}[0-9]+$";

	private static final String REG_NUMBER = "^(-|\\+)?[0-9][0-9]*(.[0-9]+)?$";

	private static final String REG_ZIP = "^[1-9][0-9]{5}$";

	private static final String REG_MOBILE = "^1[3|4|5|6|8]{1}[0-9]{9}$";

	private static final String REG_EMAIL = "^([a-zA-Z0-9_.]+)([@])([a-zA-Z0-9._]+)$";

	private static final String REG_DATE = "((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$))";

	private static final String REG_TIME = "^([0-1]?[0-9]|2[0-3]):([0-5]?[0-9])(:([0-5]?[0-9]))?$";

	private static final String IDENTITY_AREA = "11:北京,12:天津,13:河北,14:山西,15:内蒙古,21:辽宁,22:吉林,23:黑龙江,31:上海,32:江苏,33:浙江,34:安徽,35:福建,36:江西, 37:山东,41:河南,42:湖北,43:湖南,44:广东,45:广西,46:海南,50:重庆,51:四川,52:贵州,53:云南,54:西藏,61:陕西,62:甘肃,63:青海,64:宁夏,65:新疆,71:台湾,81:香港,82:澳门, 91:国外";

	private static final String REG_CHARACTER = "^[a-zA-Z0-9\\-\\_]+$";

	public static final String LICENSE_NUMBER_REG = "^[\u4e00-\u9fa5][A-Z](([0-9A-Z]{5})|([0-9A-Z]{4,5}[\u4e00-\u9fa5]))$";

	public static boolean isInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isShort(String value) {
		boolean f = matches(value, REG_INT);
		if (f) {
			try {
				Short.parseShort(value);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static boolean isLong(String value) {
		boolean f = matches(value, REG_INT);
		if (f) {
			try {
				Long.parseLong(value);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static boolean matches(String value, String reg) {
		if (isNull(value)) {
			return false;
		}
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	public static String find(String value, String reg) {
		if (isNull(value)) {
			return null;
		}
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(value);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public static boolean isDate(String value) {
		if (isNull(value)) {
			return false;
		}
		String[] dfs = new String[] { "yyyy-MM-dd HH:mm:ss",
				"yyyy-MM-dd HH:mm", "yyyy-MM-dd" };
		try {
			SimpleDateFormat dfm = null;
			for (String df : dfs) {
				dfm = new SimpleDateFormat(df);
				dfm.parse(value);
			}

			return true;
		} catch (ParseException e) {
			return false;
		}

		/*
		 * if (value == null || "".equals(value.trim())) { return false; } value
		 * = value.replaceAll("\\s+", " "); value = value.trim(); String date =
		 * value.contains(" ") ? value.split(" ")[0] : value; String time =
		 * value.contains(" ") ? value.split(" ")[1] : null; if (!matches(date,
		 * REG_DATE)) { return false; } if (time != null && time.contains("."))
		 * { if (!matches(time.split("\\.")[0], REG_TIME)) { return false; }
		 * time = time.split("\\.")[1]; if (!isInt(time)) { return false; } }
		 * else { if (time != null && !matches(time, REG_TIME)) { return false;
		 * } } return true;
		 */
	}

	public static boolean isFloat(String value) {
		if (isNull(value)) {
			return false;
		}
		boolean f = isNumber(value);
		if (f) {
			try {
				Float.parseFloat(value);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static boolean isNumber(String value) {
		return matches(value, REG_NUMBER);
	}

	public static boolean isNull(Object value) {
		return value == null || Objects.isNull(value)
				|| "".equals(value.toString().trim());
	}

	public static boolean isZip(String value) {
		return matches(value, REG_ZIP);
	}

	public static boolean isMobile(String value) {
		return matches(value, REG_MOBILE);
	}

	public static boolean isIdentity(String value) {
		if (isNull(value)) {
			return false;
		}
		if (value.length() != 15 && value.length() != 18) {
			// 长度不合法
			return false;
		}

		if (!IDENTITY_AREA.contains(value.subSequence(0, 2) + ":")) {
			// 地区不存在
			return false;
		}

		String birthday = null;
		String tailReg = "";
		String tail = "";
		if (value.length() == 15) {
			birthday = "19" + value.substring(6, 8) + "-"
					+ value.substring(8, 10) + "-" + value.substring(10, 12);
			tailReg = "^[0-9]{3}$";
			tail = value.substring(value.length() - 3);
		} else {
			birthday = value.substring(6, 10) + "-" + value.substring(10, 12)
					+ "-" + value.substring(12, 14);
			tailReg = "^[0-9]{3}[0-9xX]$";
			tail = value.substring(value.length() - 4);
		}
		// 支持（.）年.月.日，（-）年-月-日，（/）年/月/日 分割的时间格式,精确到年月日
		if (!matches(birthday, REG_DATE)) {
			// 身份证出生日期有误
			return false;
		}
		if (!matches(tail, tailReg)) {
			// 身份证含有非法字符
			return false;
		}

		return true;
	}

	public static boolean isEmail(String value) {
		return matches(value, REG_EMAIL);
	}

	/**
	 * <p>
	 * Discription:验证字符是否由字母、数字以及下划线组成
	 * </p>
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isCharacter(String value) {
		return matches(value, REG_CHARACTER);
	}

	/**
	 * <p>
	 * Discription:删除特殊符号，目前就删除“，”；
	 * </p>
	 * 
	 * @param str
	 * @return
	 */
	public static String removeSpecialSymbol(String str) {
		if (str != null) {
			str = str.replaceAll(",", "");
			str = str.replaceAll("\\\\$", "");
			str = str.replaceAll("￥", "");
		}
		return str;
	}

	/**
	 * <p>
	 * Discription:是否为正整数
	 * </p>
	 * 
	 * @param val
	 * @return
	 */
	public static boolean isPositiveInteger(String val) {
		if (isNull(val)) {
			return false;
		}
		return matches(val, POSITIVE_REG_INT);
	}

	/**
	 * <p>
	 * Discription:是否为负整数
	 * </p>
	 * 
	 * @param val
	 * @return
	 */
	public static boolean isNegativeInteger(String val) {
		if (isNull(val)) {
			return false;
		}
		return matches(val, NEGATIVE_REG_INT);
	}

	/**
	 * <p>
	 * Discription:验证数字的精度，精度有调用者自己提供，数字可以是整数，也可以是负数
	 * </p>
	 * 
	 * @param value
	 *            将被验证的数字
	 * @param scale
	 *            要求的精度，如果精度小于或者等于0，将会抛出RuntimeException异常
	 * @return
	 */
	public static boolean checkPrecisionAndScale(String value, int scale) {
		if (isNull(value)) {
			return false;
		}
		if (scale <= 0) {
			throw new RuntimeException("精度参数值scale必须为大于0的正整数！");
		}
		String reg = "^(-|\\+)?[0-9][0-9]*(.[0-9]{1," + scale + "})?$";
		return matches(value, reg);
	}

	/**
	 * <p>
	 * Discription:验证数据是否为车牌号码，目前验证的为普通车牌与特殊货车车牌，特种车牌比如军车，武警的车牌加入验证
	 * </p>
	 * 
	 * @param value
	 *            车牌号
	 * @return
	 */
	public static boolean isLicenseNumber(String value) {
		if (isNull(value)) {
			return false;
		}
		return matches(value, LICENSE_NUMBER_REG);
	}

	/**
	 * 判断集合是否为空或者集合中所有key对应的值是否为空、null、空字符串
	 * @param map
	 * @return
	 */
	public static boolean isEmpty(Map map){
		int _size = 0;
		for(Iterator iter = map.values().iterator(); iter.hasNext() ;){
			if(RegExpUtil.isNull(iter.next()) ){
				_size++;
			}
		}
		return _size == map.size();
	}

	public static String getFileExt(File file){
		if(file == null){
			return null;
		}
		String filename = file.getName();
		return getFileExt(filename);
	}

	public static String getFileExt(String filename){
		if(filename == null){
			return null;
		}
		if(filename.indexOf(".") < 0){
			return null;
		}
		String ext = filename.substring(filename.lastIndexOf("."));
		return ext;
	}

	public static void main(String[] args) {
		String filename = "aaa.xls";
		System.out.println(getFileExt(filename));
	}
}
