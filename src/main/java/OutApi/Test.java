package OutApi;

import Operation.Data_file;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.carrotsearch.sizeof.RamUsageEstimator;

import java.time.LocalDateTime;
import java.util.Date;

public class Test {
    public static void main(String[] args) {

//        mainTest();
        sizeTest();

    }


    public static void mainTest(){
        LocalDateTime startTime = LocalDateTime.of(2010,1,1,0,0);
        LocalDateTime endTime = LocalDateTime.of(2010,5,1,0,0);

        double col1 = 0;    double row1  = 0;
        double col2 = 10;   double row2 = 10;

        Long start = new Date().getTime();
        String file = Data_file.open_file("./web/data/edge.json");
        System.out.println(new Date().getTime() - start);
        System.out.println(file.length());
    }

    public static void sizeTest(){
        String origin = "{\"key\":\"name98027\",\"col\":42.1,\"row\":46.1,\"datetime\":\"2003-02-12T23:55\"}";

        JSONObject object = JSONObject.parseObject(origin);

        JSONArray array = new JSONArray();

        System.out.println("原始字符串大小为："+RamUsageEstimator.sizeOf(origin));
        System.out.println("JsonObject大小为："+RamUsageEstimator.sizeOf(object));

        System.out.println("数组原始大小为："+RamUsageEstimator.sizeOf(array));

        array.add(object);
        System.out.println("数组加入object大小为："+RamUsageEstimator.sizeOf(array));





    }


}
