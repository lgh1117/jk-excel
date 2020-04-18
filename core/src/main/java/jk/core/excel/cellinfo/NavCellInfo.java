package jk.core.excel.cellinfo;


import jk.core.ex.ExcelParseException;

/**
 * 放到Sheet前面的信息说明，一段文字说明文本,只要col与row都合法了，此栏目自动会生成
 * Created by Jack lee
 */
public class NavCellInfo extends CellInfo {

    /**
     * 占用的列数，必须大于0
     */
    private int col;
    /**
     * 占用的行数，必须大于0
     */
    private int row;
    /**
     * 需要填充的信息，为文本类型
     */
    private String info;

    /**
     * 行高
     */
    private Integer heightInPoints;

    public int getCol() {
        return col;
    }

    /**
     * 占用的列数，必须大于0
     */
    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    /**
     * 占用的行数，必须大于0
     */
    public void setRow(int row) {
        this.row = row;
    }

    public String getInfo() {
        return info;
    }

    /**
     * 需要填充的信息，为文本类型
     */
    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getHeightInPoints() {
        return heightInPoints;
    }

    public void setHeightInPoints(Integer heightInPoints) {
        this.heightInPoints = heightInPoints;
    }

    public NavCellInfo(int col, int row, String info) {
        this.col = col;
        this.row = row;
        this.info = info;
        if(col <= 0){
            throw new ExcelParseException("col必须大于0的整数");
        }
        if(row <= 0){
            throw new ExcelParseException("row必须大于0的整数");
        }
    }

    public NavCellInfo(int col, int row, String info, Integer heightInPoints) {
        this(col, row, info);
        this.heightInPoints = heightInPoints;
    }

    @Override
    public String toString() {
        return super.toString() + ";NavCellInfo{" +
                "col=" + col +
                ", row=" + row +
                ", info='" + info + '\'' +
                '}';
    }
}
