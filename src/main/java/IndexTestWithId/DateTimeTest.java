package IndexTestWithId;

import Operation.Data_file;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.time.LocalDateTime;


public class DateTimeTest {
    public static void main(String[] args) {

        String all = Data_file.open_file("testData.json");

        JSONArray jsonArray = JSONArray.parseArray(all);

        LocalDateTime start = LocalDateTime.of(2020,1,1,0,0);
        LocalDateTime end = LocalDateTime.of(2020,6,30,23,59);

        for(int i =0;i<jsonArray.size();i++){
            JSONObject object = jsonArray.getJSONObject(i);

            LocalDateTime date = LocalDateTime.parse(object.get("datetime").toString());

            if(start.isBefore(date) && end.isAfter(date)){
                System.out.println(object.get("key") + "-----" + date.toString());
            }


        }

    }



}