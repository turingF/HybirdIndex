package Bean;

import com.github.davidmoten.bplustree.BPlusTree;
import com.github.davidmoten.rtree.geometry.Rectangle;

import java.time.LocalDateTime;

public class LocStampInfo {

    Rectangle rectangle;
    private BPlusTree<LocalDateTime, ItemInfo> btree;


    public LocStampInfo(Rectangle rec,BPlusTree<LocalDateTime, ItemInfo> tree){
        rectangle = rec;
        btree = tree;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public BPlusTree<LocalDateTime, ItemInfo> getBtree() {
        return btree;
    }

    public void setBtree(BPlusTree<LocalDateTime, ItemInfo> btree) {
        this.btree = btree;
    }


}
