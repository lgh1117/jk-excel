package jk.core.excel.parse.base;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: Property.java
 * @author: Jack lee
 * @version: v1.0
 */
public class Property {

	private String name;

	private Class type;

	private String writeMethod;

	private String readMethod;

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
	 * @return the type
	 */
	public Class getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Class type) {
		this.type = type;
	}

	/**
	 * @return the writeMethod
	 */
	public String getWriteMethod() {
		return writeMethod;
	}

	/**
	 * @param writeMethod
	 *            the writeMethod to set
	 */
	public void setWriteMethod(String writeMethod) {
		this.writeMethod = writeMethod;
	}

	/**
	 * @return the readMethod
	 */
	public String getReadMethod() {
		return readMethod;
	}

	/**
	 * @param readMethod
	 *            the readMethod to set
	 */
	public void setReadMethod(String readMethod) {
		this.readMethod = readMethod;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
