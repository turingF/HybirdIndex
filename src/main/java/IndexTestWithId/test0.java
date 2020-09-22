package IndexTestWithId;


import Operation.Data_file;
import com.alibaba.fastjson.JSONArray;

public class test0 {
    public static void main(String[] args) {
        JSONArray array = JSONArray.parseArray(Data_file.open_file("./src/testData/mynodes.json"));
        System.out.println(array.size());
    }
}
