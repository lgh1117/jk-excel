package jk.core.excel.parse.chart;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: ChartItem.java
 * @author: Jack.Lee
 * @version: v1.0
 */
public class ChartItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4563650492848338566L;

	/**
	 * @param name
	 * @param value
	 */
	public ChartItem(String name, Double value) {
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * 数据项名称
	 */
	private String name;

	/***
	 * 数据项值
	 */
	private Double value;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
