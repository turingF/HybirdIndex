package Bean;

import java.time.LocalDateTime;

/**
 * 代表一个节点的"属性"信息，这里不包含空间属性，因为空间属性在R树筛选中被单独列为一个选择条件
 *
 * 另外dateTime的精确度到minute，即格式为yyyy-mm-dd T XX-xx
 */

public class ItemInfo  {
    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    private String key;
    private LocalDateTime datetime;
    private double row;
    private double col;


    public double getRow() {
        return row;
    }

    public void setRow(double row) {
        this.row = row;
    }

    public double getCol() {
        return col;
    }

    public void setCol(double col) {
        this.col = col;
    }



    public ItemInfo(String k, LocalDateTime dt, double row1,double col1){
        key = k;
        datetime = dt;
        row =row1;
        col = col1;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



}
