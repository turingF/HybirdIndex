package OutApi;

import com.alibaba.fastjson.JSONArray;
import com.carrotsearch.sizeof.RamUsageEstimator;

import java.time.LocalDateTime;
import java.util.Date;

public class EvaluateModel {

    static int endFileTime = 2020;
    static int endFileLoc = 50;

    final static String fileName = "./src/testData/2010-"+endFileTime+"_0-"+endFileLoc+"(100w).json";
//    final static String fileName = "./src/test/uidTest/uid2(20-2015-300w).json";


    public static void main(String[] args) {

        Long start = new Date().getTime();
        LocalDateTime startTime = LocalDateTime.of(2010,1,1,0,0);
        LocalDateTime endTime = LocalDateTime.of(2010,5,1,0,0);

        double col1 = 0;    double row1  = 0;
        double col2 = 10;   double row2 = 10;

        System.out.println("数据文件：2010-"+endFileTime+"_0-"+endFileLoc+"\n");

        QuickRBQuery out0 = new QuickRBQuery(fileName);

        Long middle = new Date().getTime();
        System.out.println("RB索引时间："+ (middle-start)+"ms");

        System.out.println("RB索引大小为："+ RamUsageEstimator.sizeOf(out0.getIndex())+"字节");

        System.out.println("R树深度为："+out0.getIndex().calculateDepth());

        JSONArray rs = out0.quickQuery(startTime,endTime,row1,col1,row2,col2);

        System.out.println("RB查询到长度："+rs.size());

//        System.out.println(rs.toString());

        Long end = new Date().getTime();
        System.out.println("RB查询时间："+(end-middle)+"ms");
        System.out.println();

        //-------------------------------------------------------------------------------------------------

        start = new Date().getTime();

        QuickHrQuery out = new QuickHrQuery(fileName);

        middle = new Date().getTime();
        System.out.println("BR索引时间："+ (middle-start)+"ms");
        System.out.println("RB索引大小为："+ RamUsageEstimator.sizeOf(out.getIndex())+"字节");

        rs = out.quickQuery(startTime.toLocalDate(),endTime.toLocalDate(),row1,col1,row2,col2);
        System.out.println("BR查询到长度："+rs.size());
//        System.out.println(rs.toString());

        end = new Date().getTime();
        System.out.println("BR查询时间："+(end-middle)+"ms");

        System.out.println("------------------");

    }

}
