package Bean;

public class UidLocInfo {
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public double getCol() {
        return col;
    }

    public void setCol(double col) {
        this.col = col;
    }

    public double getRow() {
        return row;
    }

    public void setRow(double row) {
        this.row = row;
    }

    int uid;
    double col;
    double row;

    public UidLocInfo(int uid, double row, double col) {
        this.uid = uid;
        this.col = col;
        this.row = row;
    }

    @Override
    public String toString() {
        return "row:"+row+" col:"+col;
    }
}
