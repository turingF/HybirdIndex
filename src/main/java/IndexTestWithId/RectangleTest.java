package IndexTestWithId;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class RectangleTest {
    public static void main(String[] args) {

        RTree<String, Rectangle> tree =  RTree.star().maxChildren(6).create();

        tree.add("8-9",Geometries.rectangleGeographic(8,8,9,9));
        tree.add("8.5-9.5",Geometries.rectangleGeographic(8.5,8.5,9.5,9.5));
        tree.add("9-10",Geometries.rectangleGeographic(9,9,10,10));
        tree.add("10-11",Geometries.rectangleGeographic(10,10,11,11));



        double row1 = 8; double row2 = 10; double col1 = 8; double col2 = 10;

        Iterable<Entry<String, Rectangle>> its = tree.search(Geometries.rectangleGeographic(0,0,10,10))
                .toBlocking().toIterable();

        for(Entry<String, Rectangle>it :its){
            System.out.println(it.value());
        }


    }



    }
