package jk.core.excel.parse.chart;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: [子系统名称]_[模块名]
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: ChartConfig.java
 * @author: Jack.Lee
 * @version: v1.0
 */
public class ChartConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2677779988572783328L;

	/**
	 * @param title
	 * @param items
	 */
	public ChartConfig(String title, List<ChartItem> items) {
		super();
		this.title = title;
		this.items = items;
	}

	/**
	 * 给柱状图，折线图用的构造方法
	 * 
	 * @param title
	 *            图标大标题
	 * @param yValue
	 *            y坐标名称
	 * @param categoryName
	 *            类目总称
	 * @param cateItems
	 *            图标类目数据，如果是柱状图，则类型必须为BarChartItem，如果是折线图，类型必须为XYChartItem
	 */
	public ChartConfig(String title, String yValue, String categoryName,
                       List cateItems) {
		this.title = title;
		this.yValue = yValue;
		this.categoryName = categoryName;
		this.cateItems = cateItems;

	}

	/**
	 * y轴名称
	 */
	private String yValue;

	/**
	 * 类目总称
	 */
	private String categoryName;

	/**
	 * 柱状图的明细数据
	 */
	private List cateItems;

	/**
	 * 生成图片宽度,单位为像素
	 */
	private int width = 640;

	/**
	 * 生成图片高度，单位为像素
	 */
	private int height = 300;

	/**
	 * x轴文字显示倾徐角度,the rotation angle (should be > 2.0)
	 */
	private double angle = 1;

	/**
	 * @return the yValue
	 */
	public String getyValue() {
		return yValue;
	}

	/**
	 * @param yValue
	 *            the yValue to set
	 */
	public void setyValue(String yValue) {
		this.yValue = yValue;
	}

	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @return the cateItems
	 */
	public List getCateItems() {
		return cateItems;
	}

	/**
	 * @param cateItems
	 *            the cateItems to set
	 */
	public void setCateItems(List cateItems) {
		this.cateItems = cateItems;
	}

	/**
	 * @param categoryName
	 *            the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * 图标标题
	 */
	private String title;

	/**
	 * 标题字体大小,默认为20
	 */
	private int titleFontSize = 20;

	/**
	 * 图片生成质量
	 */
	private float pictureQuality = 0.8f;

	/**
	 * 每项之间的间距
	 */
	private double itemMarge = 0.4;

	/**
	 * 每个类别之间的间距
	 */
	private double categoryMarge = 0.2;

	/**
	 * 标题字体,默认为"黑体"
	 */
	private String titleFontName = "黑体";

	/**
	 * 图列标签字体大小,默认为12
	 */
	private int labelFontSize = 12;

	/**
	 * 图列标签字体,默认为“宋体”
	 */
	private String labelFontName = "宋体";

	/**
	 * 明细项字体大小，默认为14
	 */
	private int itemFontSize = 14;

	/**
	 * 明细项字体,默认为“宋体”
	 */
	private String itemFontName = "宋体";

	/**
	 * 明细项目数据
	 */
	private List<ChartItem> items;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the titleFontSize
	 */
	public int getTitleFontSize() {
		return titleFontSize;
	}

	/**
	 * @param titleFontSize
	 *            the titleFontSize to set
	 */
	public void setTitleFontSize(int titleFontSize) {
		this.titleFontSize = titleFontSize;
	}

	/**
	 * @return the titleFontName
	 */
	public String getTitleFontName() {
		return titleFontName;
	}

	/**
	 * @param titleFontName
	 *            the titleFontName to set
	 */
	public void setTitleFontName(String titleFontName) {
		this.titleFontName = titleFontName;
	}

	/**
	 * @return the labelFontSize
	 */
	public int getLabelFontSize() {
		return labelFontSize;
	}

	/**
	 * @param labelFontSize
	 *            the labelFontSize to set
	 */
	public void setLabelFontSize(int labelFontSize) {
		this.labelFontSize = labelFontSize;
	}

	/**
	 * @return the labelFontName
	 */
	public String getLabelFontName() {
		return labelFontName;
	}

	/**
	 * @param labelFontName
	 *            the labelFontName to set
	 */
	public void setLabelFontName(String labelFontName) {
		this.labelFontName = labelFontName;
	}

	/**
	 * @return the itemFontSize
	 */
	public int getItemFontSize() {
		return itemFontSize;
	}

	/**
	 * @param itemFontSize
	 *            the itemFontSize to set
	 */
	public void setItemFontSize(int itemFontSize) {
		this.itemFontSize = itemFontSize;
	}

	/**
	 * @return the itemFontName
	 */
	public String getItemFontName() {
		return itemFontName;
	}

	/**
	 * @param itemFontName
	 *            the itemFontName to set
	 */
	public void setItemFontName(String itemFontName) {
		this.itemFontName = itemFontName;
	}

	/**
	 * @return the items
	 */
	public List<ChartItem> getItems() {
		return items;
	}

	/**
	 * @return the angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @param angle
	 *            the angle to set
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(List<ChartItem> items) {
		this.items = items;
	}

	/**
	 * @return the pictureQuality
	 */
	public float getPictureQuality() {
		return pictureQuality;
	}

	/**
	 * @param pictureQuality
	 *            the pictureQuality to set
	 */
	public void setPictureQuality(float pictureQuality) {
		this.pictureQuality = pictureQuality;
	}

	/**
	 * @return the itemMarge
	 */
	public double getItemMarge() {
		return itemMarge;
	}

	/**
	 * @param itemMarge
	 *            the itemMarge to set
	 */
	public void setItemMarge(double itemMarge) {
		this.itemMarge = itemMarge;
	}

	/**
	 * @return the categoryMarge
	 */
	public double getCategoryMarge() {
		return categoryMarge;
	}

	/**
	 * @param categoryMarge
	 *            the categoryMarge to set
	 */
	public void setCategoryMarge(double categoryMarge) {
		this.categoryMarge = categoryMarge;
	}

	public void addItem(ChartItem item) {
		if (this.items == null) {
			this.items = new ArrayList<ChartItem>();
		}
		if (item == null) {
			return;
		}
		items.add(item);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
