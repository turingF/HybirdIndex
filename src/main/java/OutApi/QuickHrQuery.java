package OutApi;

import Bean.StampInfo;
import Operation.CreateIndex;
import Operation.Data_file;
import Operation.Query;
import com.alibaba.fastjson.JSONArray;
import com.github.davidmoten.bplustree.BPlusTree;

import java.time.LocalDate;

/**
 * 对外接口，new一个该类
 */
public class QuickHrQuery {

    private BPlusTree<LocalDate, StampInfo> index;

    public BPlusTree<LocalDate, StampInfo> getIndex() {
        return index;
    }

    public JSONArray quickQuery(LocalDate start, LocalDate end, double row1, double col1, double row2, double col2){

        if (index ==null){
            System.out.println("请先创建索引");
        }

        return Query.query4hr(index,start,end,row1,col1,row2,col2);
    }

    private void createIndex(String fileName){

        String origin = Data_file.open_file(fileName);
        index = CreateIndex.createHRIndex(JSONArray.parseArray(origin));
    }

    private void createIndex(JSONArray jsonArray){
        index = CreateIndex.createHRIndex(jsonArray);
    }

    public QuickHrQuery(String fileName){
        createIndex(fileName);
    }

    public QuickHrQuery(JSONArray jsonArray) {createIndex(jsonArray);}

}
