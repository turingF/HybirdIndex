package Bean;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;

import java.time.LocalDate;

public class StampInfo {

    private LocalDate date;
    private RTree<ItemInfo, Point> rTree;

    public LocalDate getDatetime() {
        return date;
    }

    public void setDatetime(LocalDate date) {
        this.date = date;
    }

    public StampInfo(LocalDate dt, RTree<ItemInfo, Point> tree){
        this.date = dt;
        this.rTree = tree;
    }

    StampInfo(){

    }


    public RTree<ItemInfo, Point> getrTree() {
        return rTree;
    }

    public void setrTree(RTree<ItemInfo, Point> rTree) {
        this.rTree = rTree;
    }




}
