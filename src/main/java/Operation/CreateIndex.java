package Operation;

import Bean.ItemInfo;
import Bean.LocStampInfo;
import Bean.StampInfo;
import com.alibaba.fastjson.JSON;
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

public class CreateIndex {


    /**
     * 根据json数组创建空间索引R树
     * @param origin 原始数据，即json数组
     * @return 创建的R树
     */
    public static RTree<ItemInfo, Point> createLocIndex(JSONArray origin){

        RTree<ItemInfo, Point> tree =  RTree.star().maxChildren(6).create();
        // --- 创建r树索引文件 ------
        for(int i =0;i<origin.size();i++){

            JSONObject object = origin.getJSONObject(i);

            //经纬度
            double row = Double.parseDouble(object.get("row").toString());
            double col = Double.parseDouble(object.get("col").toString());
            Point lc = Geometries.point(row,col);

            tree=tree.add(new ItemInfo(object.get("key").toString(), LocalDateTime.parse(object.get("datetime").toString()),row,col),lc);
        }

        return tree;

    }

    /**
     * 根据json数组创建时间B树
     * @param origin 原始数据，即json数组
     * @return 创建的B树
     */
    public static BPlusTree<LocalDateTime,ItemInfo> createTimeIndex(JSONArray origin){

        BPlusTree<LocalDateTime, ItemInfo> tree =
                BPlusTree
                        .memory()
                        .maxLeafKeys(32)
                        .maxNonLeafKeys(8)
                        .naturalOrder();

        for(int i =0;i<origin.size();i++){

            JSONObject object = origin.getJSONObject(i);
            double row = Double.parseDouble(object.get("row").toString());
            double col = Double.parseDouble(object.get("col").toString());
            LocalDateTime dt = LocalDateTime.parse(object.get("datetime").toString());


            tree.insert(dt,new ItemInfo(object.get("key").toString(),dt,row,col));
        }


        return tree;
    }

    /**
     * 根据json数组创建，以时间的B树为基，B树节点挂着每个时间片的R树（时间片为一天）
     * @param origin 原始数据，即json数组
     * @return 创建的B树
     */
    public static BPlusTree<LocalDate, StampInfo> createHRIndex(JSONArray origin){
        BPlusTree<LocalDate, StampInfo> tree =
                BPlusTree
                        .memory()
                        .maxLeafKeys(32)
                        .maxNonLeafKeys(8)
                        .naturalOrder();

        //将数据的时间分为不同的时间片,这里假设以天作为分片单位
        Map<LocalDate,ArrayList<ItemInfo>> dateSet = new HashMap<>();

        for(int i =0;i<origin.size();i++){

            JSONObject object = origin.getJSONObject(i);
            //解析object的数据格式
            LocalDateTime dt = LocalDateTime.parse(object.get("datetime").toString());
            double row = Double.parseDouble(object.get("row").toString());
            double col = Double.parseDouble(object.get("col").toString());

            LocalDate date = dt.toLocalDate(); //抽取时间片

            if(dateSet.get(date)!=null){
                ArrayList<ItemInfo> stampList = dateSet.get(date);
                stampList.add(new ItemInfo(object.get("key").toString(),dt,row,col));
                dateSet.put(date,stampList);
            }else {
                ArrayList<ItemInfo> stamplist = new ArrayList<>();
                stamplist.add(new ItemInfo(object.get("key").toString(),dt,row,col));
                dateSet.put(date,stamplist);
            }
        }

        System.out.println("BR子树大小为："+dateSet.size());

        //根据每个时间片内构建R树
        Iterator<Map.Entry<LocalDate, ArrayList<ItemInfo>>> iter = dateSet.entrySet().iterator();
        Map.Entry<LocalDate, ArrayList<ItemInfo>> entry;



        while (iter.hasNext()) {
            entry = iter.next();
            ArrayList<ItemInfo> stampList = entry.getValue();

            JSONArray temp = JSONArray.parseArray(JSON.toJSONString(stampList));
            RTree<ItemInfo, Point> rTree = CreateIndex.createLocIndex(temp);

            StampInfo stampInfo = new StampInfo(entry.getKey(),rTree);
            tree.insert(entry.getKey(),stampInfo);
        }

            return tree;

    }

    /**
     * 根据json数组创建，以空间的R树为基，R树节点挂着每个地点片的B树（空间片为(X,Y)=[x,y,x+1,y+1]）
     * @param origin 原始数据，即json数组
     * @return 创建的R树
     */
    public static RTree<LocStampInfo, Rectangle> createRBtree(JSONArray origin){
        RTree<LocStampInfo, Rectangle> tree =  RTree.star().maxChildren(6).create();

        //以区域作为分割片，假设这里分割间距为
        Map<Rectangle,ArrayList<ItemInfo>> dateSet = new HashMap<>();

        for (int i=0;i<origin.size();i++){

            JSONObject object = origin.getJSONObject(i);
            //解析object的数据格式
            LocalDateTime dt = LocalDateTime.parse(object.get("datetime").toString());
            double row = Double.parseDouble(object.get("row").toString());
            double col = Double.parseDouble(object.get("col").toString());

            //抽取整数值为空间片
            int rows = (int)row;
            int cols = (int)col;
            double minn=0.001;//无穷小

            //*这里需要加一个低于最小精度的变量，因为比如搜索[0,10]，而10.xx会被归为[10,11]类，从而与[0,10]相交而被选择，
            //故需要把10.xx归到(10,11]类

            Rectangle rec = Geometries.rectangle(rows+minn,cols+minn,rows+1,cols+1);

            if(dateSet.get(rec)!=null){

                ArrayList<ItemInfo> stampList = dateSet.get(rec);
                stampList.add(new ItemInfo(object.get("key").toString(),dt,row,col));
                dateSet.put(rec,stampList);
            }else {
                ArrayList<ItemInfo> stamplist = new ArrayList<>();
                stamplist.add(new ItemInfo(object.get("key").toString(),dt,row,col));
                dateSet.put(rec,stamplist);
            }

        }

        System.out.println("RB的子树个数为："+dateSet.size());

        //根据每个时间片内构建R树
        Iterator<Map.Entry<Rectangle,ArrayList<ItemInfo>>> iter = dateSet.entrySet().iterator();
        Map.Entry<Rectangle, ArrayList<ItemInfo>> entry;

        while (iter.hasNext()) {
            entry = iter.next();
            ArrayList<ItemInfo> stampList = entry.getValue();

            JSONArray temp = JSONArray.parseArray(JSON.toJSONString(stampList));

            BPlusTree<LocalDateTime,ItemInfo> bTree = CreateIndex.createTimeIndex(temp);

            LocStampInfo locStampInfo = new LocStampInfo(entry.getKey(),bTree);

            tree = tree.add(locStampInfo,entry.getKey());
        }



        return tree;
    }





}
