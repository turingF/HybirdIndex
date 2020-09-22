package Operation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * 生成测试数据集，初始化该类传入数据条目max和文件名fn，之后调用produce()方法生成数据
 */
public class ProduceTestData {

    enum type
    {
        军舰,潜艇,飞机,船只
    }


    int max_num = 0; //数据总量初始化
    final static int max_type_id = 50; //type_id为类型编号,即飞机1，2，3···


    final static int max_space = 20; //最大经纬度
    final static int low_year = 2010,high_year=2012;



    String fileName = "testData.json"; //测试数据文件名

    public ProduceTestData(int max,String fn){

        max_num = max;
        fileName = fn;

    }

    public void produce(){
        int type_size = type.values().length;

        JSONArray testArray = new JSONArray();


        String all = "";

        //---------生成测试数据-----------------
        for(int i =0;i<max_num;i++){

            //随机生成key,datetime,space属性
            JSONObject testObj = new JSONObject();

            testObj.put("uid",i);
            testObj.put("key", type.values()[new Random().nextInt(type_size)].toString()+new Random().nextInt(max_type_id));
            testObj.put("col",RandomNextDouble());
            testObj.put("row",RandomNextDouble());
            testObj.put("datetime",RandomNextDateTime());

            all+=testObj.toString()+"\n";

        }

        //-----------------------------------

        Data_file.writeFile(fileName,all);

    }

    /**
     * 随机生成一个小数点后两位的数作为经纬度,范围为[0-max_space]
     * @return
     */

    public static double RandomNextDouble(){

        double rs = 0;
        DecimalFormat df = new DecimalFormat( "0.00" );
        rs = new Random().nextInt(max_space) + Double.parseDouble(df.format(new Random().nextDouble()));
        return rs;
    }

    /**
     *随机生成一个日期，为标准ISO格式:yyyy-MM-ddTHH:mm，精确到分钟
     */
    public static String RandomNextDateTime(){
        String rs = "";


        int randomYear = low_year + new Random().nextInt(high_year-low_year+1);
        int randomMonth = new Random().nextInt(12)+1;
        int randomDay = new Random().nextInt(28)+1;
        int randomHour = new Random().nextInt(24);
        int randomMin = new Random().nextInt(60);

        rs = LocalDateTime.of(randomYear,randomMonth,randomDay,randomHour,randomMin).toString();

        return rs;
    }
}
