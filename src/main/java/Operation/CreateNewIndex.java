package Operation;

import Bean.UidLocInfo;
import Bean.UidTimeInfo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.davidmoten.bplustree.BPlusTree;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CreateNewIndex {

    public static RTree<Integer, Point> createRtree_uid(JSONArray origin){
        RTree<Integer, Point> tree =  RTree.star().maxChildren(6).create();

        for(int i =0;i<origin.size();i++){

            JSONObject object = origin.getJSONObject(i);

            //经纬度
            double row = Double.parseDouble(object.get("row").toString());
            double col = Double.parseDouble(object.get("col").toString());
            Point lc = Geometries.point(row,col);

            int uid = Integer.parseInt(object.getString("uid"));

            tree=tree.add(uid,lc);
        }

        return tree;
    }

    public static BPlusTree<LocalDateTime,Integer> createBtree_uid(JSONArray origin){

        BPlusTree<LocalDateTime, Integer> tree =
                BPlusTree
                        .memory()
                        .maxLeafKeys(32)
                        .maxNonLeafKeys(8)
                        .naturalOrder();

        for(int i =0;i<origin.size();i++){

            JSONObject object = origin.getJSONObject(i);

            int uid = object.getInteger("uid");
            LocalDateTime dt = LocalDateTime.parse(object.get("datetime").toString());


            tree.insert(dt,uid);
        }


        return tree;

    }

    public static BPlusTree<LocalDate, RTree<Integer, Point>> createBRtree_uid(JSONArray origin){
        BPlusTree<LocalDate, RTree<Integer, Point>> btree =
                BPlusTree
                        .memory()
                        .maxLeafKeys(32)
                        .maxNonLeafKeys(8)
                        .naturalOrder();

        //将数据的时间分为不同的时间片,这里假设以天作为分片单位
        Map<LocalDate,ArrayList<UidLocInfo>> dateSet = new HashMap<>();

        for(int i =0;i<origin.size();i++){

            JSONObject object = origin.getJSONObject(i);
            //解析object的数据格式
            LocalDateTime dt = LocalDateTime.parse(object.get("datetime").toString());
            int uid = object.getInteger("uid");
            double row = object.getDouble("row");
            double col = object.getDouble("col");

            LocalDate date = dt.toLocalDate(); //抽取时间片

            if(dateSet.get(date)!=null){
                ArrayList<UidLocInfo> stampList = dateSet.get(date);
                stampList.add(new UidLocInfo(uid,row,col));
                dateSet.put(date,stampList);
            }else {
                ArrayList<UidLocInfo> stamplist = new ArrayList<>();
                stamplist.add(new UidLocInfo(uid,row,col));
                dateSet.put(date,stamplist);
            }
        }

        System.out.println("BR子树大小为："+dateSet.size());

        //根据每个时间片内构建R树
        Iterator<Map.Entry<LocalDate, ArrayList<UidLocInfo>>> iter = dateSet.entrySet().iterator();
        Map.Entry<LocalDate, ArrayList<UidLocInfo>> entry;



        while (iter.hasNext()) {
            entry = iter.next();
            ArrayList<UidLocInfo> stampList = entry.getValue();

            //根据list构建R树
            RTree<Integer, Point> rTree =  RTree.star().maxChildren(6).create();
            for(UidLocInfo info : stampList){
                int uid = info.getUid();

                Point point = Geometries.point(info.getRow(),info.getCol());
                rTree = rTree.add(uid,point);

            }

            btree.insert(entry.getKey(),rTree);
        }

        return btree;
    }

    public static RTree<BPlusTree<LocalDateTime,Integer>, Rectangle> createRBtree_uid(JSONArray origin){
        RTree<BPlusTree<LocalDateTime,Integer>, Rectangle> tree =  RTree.star().minChildren(3).maxChildren(6).create();

        //将数据的时间分为不同的时间片,这里假设以天作为分片单位
        Map<Rectangle, ArrayList<UidTimeInfo>> dateSet = new HashMap<>();

        for (int i=0;i<origin.size();i++){

            JSONObject object = origin.getJSONObject(i);
            //解析object的数据格式

            double row = Double.parseDouble(object.get("row").toString());
            double col = Double.parseDouble(object.get("col").toString());

            LocalDateTime dt = LocalDateTime.parse(object.get("datetime").toString());

            int uid = object.getInteger("uid");
            //抽取整数值为空间片
            int rows = (int)row;
            int cols = (int)col;
            double minn=0.001;//无穷小

            //*这里需要加一个低于最小精度的变量，因为比如搜索[0,10]，而10.xx会被归为[10,11]类，从而与[0,10]相交而被选择，
            //故需要把10.xx归到(10,11]类

            Rectangle rec = Geometries.rectangle(rows+minn,cols+minn,rows+1,cols+1);

            if(dateSet.get(rec)!=null){

                ArrayList<UidTimeInfo> stampList = dateSet.get(rec);
                stampList.add(new UidTimeInfo(uid,dt));
                dateSet.put(rec,stampList);
            }else {
                ArrayList<UidTimeInfo> stamplist = new ArrayList<>();
                stamplist.add(new UidTimeInfo(uid,dt));
                dateSet.put(rec,stamplist);
            }

        }

        System.out.println("RB的子树个数为："+dateSet.size());

        //根据每个时间片内构建R树
        Iterator<Map.Entry<Rectangle,ArrayList<UidTimeInfo>>> iter = dateSet.entrySet().iterator();
        Map.Entry<Rectangle, ArrayList<UidTimeInfo>> entry;

        while (iter.hasNext()) {
            entry = iter.next();
            ArrayList<UidTimeInfo> stampList = entry.getValue();

            //根据list构建b树
            BPlusTree<LocalDateTime, Integer> bTree =
                    BPlusTree
                            .memory()
                            .maxLeafKeys(32)
                            .maxNonLeafKeys(8)
                            .naturalOrder();

            for(UidTimeInfo info:stampList){
                bTree.insert(info.getDate(),info.getUid());
            }

            tree = tree.add(bTree,entry.getKey());
        }
        return tree;
    }




}
