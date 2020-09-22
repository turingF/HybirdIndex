package OutApi;

import Bean.LocStampInfo;
import Operation.CreateIndex;
import Operation.Data_file;
import Operation.Query;
import com.alibaba.fastjson.JSONArray;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Rectangle;

import java.time.LocalDateTime;

public class QuickRBQuery {

    private RTree<LocStampInfo, Rectangle> index;

    public QuickRBQuery(String fileName) {createIndex(fileName);}

    public RTree<LocStampInfo, Rectangle> getIndex() {
        return index;
    }

    private void createIndex(String fileName){

        String origin = Data_file.open_file(fileName);
        index = CreateIndex.createRBtree(JSONArray.parseArray(origin));
    }

    public JSONArray quickQuery(LocalDateTime start, LocalDateTime end, double row1, double col1, double row2, double col2){

        if (index ==null){
            System.out.println("请先创建索引");
        }

        return Query.query4RB(index,start,end,row1,col1,row2,col2);
    }


}
