package jk.core;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 */
public interface Excel {

	/**
	 * <p>
	 * Discription:解析为map，map的key为提供的header中的name，value为对应的excel值，全为string类型
	 * </p>
	 * 
	 * @return
	 */
	public List<Map> parseToMapList();

	/**
	 * <p>
	 * Discription:解析为提供的实体类T
	 * </p>
	 * 
	 * @param t
	 * @return
	 */
	public <T> List<T> parseToList(Class<T> t);

	/**
	 * <p>
	 * Discription:解析所有的sheet数据，返回每个sheet中的数据
	 * </p>
	 * 
	 * @return
	 */
	public Map<String, List<Map>> parseAllSheetToMapList();

	/**
	 * 只做数据解析，并把数据传递给监听器，不做数据存储返回
	 */
	void parse();

	String START = "start";
	String END = "end";
	String END_HEADER = "endheader";
}
