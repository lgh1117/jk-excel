package jk.core.excel.parse.chart;

import java.io.Serializable;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: 折线图坐标值
 * </p>
 * 
 * @file: LienItem.java
 * @author: Jack.Lee
 * @version: v1.0
 */
public class XYItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5490064830753020994L;

	private Object x;

	private double y;

	public XYItem(Object x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public Object getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(Object x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

}
