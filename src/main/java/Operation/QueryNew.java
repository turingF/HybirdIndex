package Operation;

import com.alibaba.fastjson.JSONArray;
import com.github.davidmoten.bplustree.BPlusTree;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class QueryNew {

    public static ArrayList<Integer> queryR_uid(RTree<Integer, Point> tree, double row1, double col1, double row2, double col2){

        ArrayList<Integer> list = new ArrayList<>();

        Iterable<Entry<Integer, Point>> its = tree.search(Geometries.rectangle(row1,col1,row2,col2))
                .toBlocking().toIterable();

        //将查询结果存到结果数组里
        for(Entry<Integer, Point>it :its){
            list.add(it.value());
        }

        return list;
    }

    public static ArrayList<Integer> queryB_uid(BPlusTree<LocalDateTime,Integer> tree, LocalDateTime start, LocalDateTime end){

        ArrayList<Integer> list = new ArrayList<>();
        tree.findEntries(start, end).forEach(
                (K)-> {
                    list.add(K.value());
                }
        );

        return list;

    }

    public static ArrayList<Integer> queryRB_uid(RTree<BPlusTree<LocalDateTime,Integer>, Rectangle>  br,LocalDateTime start,LocalDateTime end,double row1, double col1, double row2, double col2){

        ArrayList<Integer> list = new ArrayList<>();
        Iterable<Entry<BPlusTree<LocalDateTime,Integer>, Rectangle>> its = br.search(Geometries.rectangle(row1,col1,row2,col2))
                .toBlocking().toIterable();


        for(Entry<BPlusTree<LocalDateTime,Integer>, Rectangle>it :its){

            BPlusTree<LocalDateTime,Integer> btree = it.value();

            ArrayList<Integer> tempResult = QueryNew.queryB_uid(btree,start,end);

            if(tempResult.size()!=0){

                for (int i =0;i<tempResult.size();i++){
                    list.add(tempResult.get(i)); //将查询结果汇总到一个数组
                }
            }

        }

        return list;
    }

    public static ArrayList<Integer> queryBR_uid(BPlusTree<LocalDate, RTree<Integer, Point>> hr, LocalDate start, LocalDate end, double row1, double col1, double row2, double col2){

        ArrayList<Integer> list = new ArrayList<>();
        hr.findEntries(start, end).forEach(
                (K)-> {
                    RTree<Integer, Point> rTree = K.value();


//                    System.out.println(RamUsageEstimator.sizeOf(RamUsageEstimator.sizeOf(rTree)));

                    //对每颗r树都要查询
                    ArrayList<Integer> tempResult = QueryNew.queryR_uid(rTree, row1, col1, row2, col2);



                    //如果有查询结果
                    if (tempResult.size() != 0) {

//                        System.out.println("时间片："+info.getDatetime());
//                        System.out.println("R树MBR查询路线：");

                        for (int i = 0; i < tempResult.size(); i++) {
                            list.add(tempResult.get(i)); //将查询结果汇总到一个数组
                        }

                        //针对每个查询结果打印轨迹
//                        String allPath = rTree.asString();
//                        selectOutput(allPath,tempResult);
                    }

                });

        return list;
    }

    public static JSONArray trans_uid2info(ArrayList<Integer> uidList){

        JSONArray array = new JSONArray();




        return array;

    }


}
