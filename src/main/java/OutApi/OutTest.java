package OutApi;

import com.alibaba.fastjson.JSONArray;
import com.carrotsearch.sizeof.RamUsageEstimator;

import java.time.LocalDate;

public class OutTest {
    public static void main(String[] args) {

        //先创建一个查询类，创建过程中会创建索引，该类需要保存在内存中，以便查询的时候可以直接找到索引


        //索引创建可通过打开本地文件或传入jsonarray，之后需要保证该类一直在
        QuickHrQuery out = new QuickHrQuery("web/data/node.json"); //打开本地文件创立索引，文件格式是jsonarray
        //QuickHrQuery out = new QuickHrQuery(new JSONArray()); //当然也可以直接传入jsonarray建立索引


        //需要查询时直接调用该类的query方法即可，参数为起始时间，终止时间，col1,row1,col2,row2

        LocalDate start = LocalDate.of(2010,1,1); //下面是测试查询数据
        LocalDate end = LocalDate.of(2019,5,1);

        double col1 = 0;    double row1 = 0;
        double col2 = 5;   double row2 = 5;


        System.out.println("index大小："+ RamUsageEstimator.sizeOf(out.getIndex()));
        //查询结果为jsonarray,可以将各个节点jsonarray合并为最后结果
        JSONArray result = out.quickQuery(start,end,row1,col1,row2,col2);

        //查询结果显示
        System.out.println("查询结果为：");
        System.out.println(result);

    }
}
