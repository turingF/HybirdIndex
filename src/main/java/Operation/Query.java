package Operation;

import Bean.ItemInfo;
import Bean.LocStampInfo;
import Bean.StampInfo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.davidmoten.bplustree.BPlusTree;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Query {

    /**
     * 对空间信息进行查询
     * @param tree
     * @param row1
     * @param col1
     * @param row2
     * @param col2
     * @return
     */
    public static JSONArray query4location(RTree<ItemInfo, Point> tree, double row1, double col1, double row2, double col2){

        JSONArray rs = new JSONArray();

        Iterable<Entry<ItemInfo, Point>> its = tree.search(Geometries.rectangle(row1,col1,row2,col2))
                .toBlocking().toIterable();

        //将查询结果存到结果数组里
        for(Entry<ItemInfo, Point>it :its){
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(it.value()));
            rs.add(jsonObject);
        }



        return rs;

    }


    /**
     * 对时间进行查询
     * @param tree 已经建立的b+树
     * @param start 起始时间
     * @param end  终止时间
     * @return
     */
    public static JSONArray query4date(BPlusTree<LocalDateTime, ItemInfo> tree, LocalDateTime start, LocalDateTime end){

        JSONArray rs = new JSONArray();

        tree.findEntries(start, end).forEach(
                (K)-> {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(K.value()));
                    rs.add(jsonObject);
                }
        );


        return rs;
    }

    /**
     * 对轨迹的查询
     * @param origin
     * @param key
     * @return
     */
    public static JSONArray query4curve(JSONArray origin,String key){

        JSONArray rs = new JSONArray();

        for(int i =0;i<origin.size();i++){

            JSONObject object = origin.getJSONObject(i);

            if(object.get("key").toString().equals(key)){
                rs.add(object);
            }
        }


        return rs;

    }

    /**
     * 对周边舰艇的查询
     * @param tree
     * @param row1
     * @param col1
     * @param r
     * @return
     */
    public static JSONArray query4around(RTree<ItemInfo, Point> tree, double row1, double col1, double r){

        JSONArray rs = new JSONArray();

        Iterable<Entry<ItemInfo, Point>> its = tree.search(Geometries.point(row1,col1),r)
                .toBlocking().toIterable();

        for(Entry<ItemInfo, Point>it :its){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key",it.value().getKey());
            jsonObject.put("datetime",it.value().getDatetime());
            jsonObject.put("row",it.value().getRow());
            jsonObject.put("col",it.value().getCol());

            rs.add(jsonObject);
        }

        return rs;
    }

    public static JSONArray query4RB(RTree<LocStampInfo, Rectangle> br, LocalDateTime start, LocalDateTime end, double row1, double col1, double row2, double col2){
        JSONArray rs = new JSONArray();

        Iterable<Entry<LocStampInfo, Rectangle>> its = br.search(Geometries.rectangle(row1,col1,row2,col2))
                .toBlocking().toIterable();


        for(Entry<LocStampInfo, Rectangle>it :its){

           LocStampInfo stamp = it.value();
           BPlusTree<LocalDateTime, ItemInfo> btree = stamp.getBtree();

            JSONArray tempResult = Query.query4date(btree,start,end);

            if(tempResult.size()!=0){

                for (int i =0;i<tempResult.size();i++){
                    rs.add(tempResult.getJSONObject(i)); //将查询结果汇总到一个数组
                }
            }

        }


        return rs;
    }

    /**
     * 使用时间和空间属性进行查询
     * @param hr
     * @param start
     * @param end
     * @param row1
     * @param col1
     * @param row2
     * @param col2
     * @return
     */
    public static JSONArray query4hr(BPlusTree<LocalDate, StampInfo> hr, LocalDate start, LocalDate end, double row1, double col1, double row2, double col2){
        JSONArray rs = new JSONArray();

//        System.out.println("根据时间查询到的R树有：");
        //先进行时间查询，找到对应的r树树群
        hr.findEntries(start, end).forEach(
                (K)-> {
                    StampInfo info = K.value();

                    RTree<ItemInfo, Point> rTree= info.getrTree();


                    //对每颗r树都要查询
                    JSONArray tempResult = Query.query4location(rTree,row1,col1,row2,col2);

                    //如果有查询结果
                    if (tempResult.size()!=0){

//                        System.out.println("时间片："+info.getDatetime());
//                        System.out.println("R树MBR查询路线：");

                        for (int i =0;i<tempResult.size();i++){
                            rs.add(tempResult.getJSONObject(i)); //将查询结果汇总到一个数组
                        }

                        //针对每个查询结果打印轨迹
//                        String allPath = rTree.asString();
//                        selectOutput(allPath,tempResult);
                    }



                }
        );

        return rs;
    }

    public static void outputPath(BPlusTree<LocalDate, StampInfo> hr, LocalDate start, LocalDate end, double row1, double col1, double row2, double col2){

        System.out.println("根据时间查询到的R树有：");
        //先进行时间查询，找到对应的r树树群
        hr.findEntries(start, end).forEach(
                (K)-> {
                    StampInfo info = K.value();

                    RTree<ItemInfo, Point> rTree= info.getrTree();


                    //对每颗r树都要查询
                    JSONArray tempResult = Query.query4location(rTree,row1,col1,row2,col2);

                    //如果有查询结果
                    if (tempResult.size()!=0){

                        System.out.println("时间片："+info.getDatetime());
                        System.out.println("R树MBR查询路线：");

                        //针对每个查询结果打印轨迹
                        String allPath = rTree.asString();
                        selectOutput(allPath,tempResult);
                    }



                }
        );

    }

    public static void selectOutput(String all,JSONArray rs){


        String [] results = all.split("\n");

        for (String line:results){

            if (line.contains("entry")){

                for(int i=0;i<rs.size();i++){

                    if(line.contains("x="+rs.getJSONObject(i).get("row")+", y="+rs.getJSONObject(i).get("col"))){
                        System.out.println(line);
                    }
                }

            }else {
                System.out.println(line);
            }

        }


        System.out.println("------------------------------------------------------------");

    }
}
