package IndexTestWithId.uidTest;

import Operation.CreateNewIndex;
import Operation.Data_file;
import Operation.QueryNew;
import com.alibaba.fastjson.JSONArray;
import com.carrotsearch.sizeof.RamUsageEstimator;
import com.github.davidmoten.bplustree.BPlusTree;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class uidEvaluateModel {

    final static String fileName = "./src/test/uidTest/uid2(20-2015-300w).json";

    public static void main(String[] args) {
        Long start = new Date().getTime();
        LocalDateTime startTime = LocalDateTime.of(2010,1,1,0,0);
        LocalDateTime endTime = LocalDateTime.of(2010,5,1,0,0);

        double col1 = 0;    double row1  = 0;
        double col2 = 10;   double row2 = 10;

        String file = Data_file.open_file(fileName);

        BPlusTree<LocalDate, RTree<Integer, Point>> index1 = CreateNewIndex.createBRtree_uid(JSONArray.parseArray(file));

        Long middle = new Date().getTime();
        System.out.println("BR索引时间："+ (middle-start)+"ms");
        System.out.println("BR索引大小为："+ RamUsageEstimator.sizeOf(index1)+"字节");

        Long middle2 = new Date().getTime();
        ArrayList<Integer> resultUid = QueryNew.queryBR_uid(index1,startTime.toLocalDate(),endTime.toLocalDate(),row1,col1,row2,col2);
        System.out.println("BR查询到长度："+resultUid.size());

        Long end = new Date().getTime();
        System.out.println("BR查询时间："+(end-middle2)+"ms");
        System.out.println();


        //-------------------------------------------\

        start = new Date().getTime();
        RTree<BPlusTree<LocalDateTime,Integer>, Rectangle> index2 = CreateNewIndex.createRBtree_uid(JSONArray.parseArray(file));

        middle = new Date().getTime();
        System.out.println("RB索引时间："+ (middle-start)+"ms");
        System.out.println("RB索引大小为："+ RamUsageEstimator.sizeOf(index2)+"字节");

        System.out.println("R树深度为："+index2.calculateDepth());

        middle2 = new Date().getTime();
        ArrayList<Integer> resultUid2 = QueryNew.queryRB_uid(index2,startTime,endTime,row1,col1,row2,col2);
        System.out.println("RB查询到长度："+resultUid2.size());

        end = new Date().getTime();
        System.out.println("RB查询时间："+(end-middle2)+"ms");
        System.out.println();


    }
}
