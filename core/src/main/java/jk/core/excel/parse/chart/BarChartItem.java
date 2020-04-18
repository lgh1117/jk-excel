package jk.core.excel.parse.chart;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: 某个名称下柱状图数据
 * </p>
 * 
 * @file: BarChartItem.java
 * @author: Jack.Lee
 * @version: v1.0
 */
public class BarChartItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8684766768673213398L;

	public BarChartItem(String category, List<ChartItem> items) {
		this.category = category;
		this.items = items;
	}

	/**
	 * 类目名称
	 */
	private String category;

	/**
	 * 展示值
	 */
	private List<ChartItem> items;

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the items
	 */
	public List<ChartItem> getItems() {
		return items;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(List<ChartItem> items) {
		this.items = items;
	}

}
