package jk.core.excel.parse.chart;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: 一个类目的折线图数据
 * </p>
 * 
 * @file: XYChartItem.java
 * @author: Jack.Lee
 * @version: v1.0
 */
public class LineItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1336550561518287971L;

	private String name;

	private List<XYItem> items;

	public LineItem(String name, List<XYItem> items) {
		this.name = name;
		this.items = items;
	}

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
	 * @return the items
	 */
	public List<XYItem> getItems() {
		return items;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(List<XYItem> items) {
		this.items = items;
	}

}
