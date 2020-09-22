package IndexTestWithId.uidTest;

import Operation.CreateNewIndex;
import Operation.Data_file;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.davidmoten.bplustree.BPlusTree;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;
import org.neo4j.driver.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.neo4j.driver.Values.parameters;

public class Test {

    final static String fileName = "./src/test/uidTest/uid1.json";

    final static String uri = "bolt://localhost:7687";
    final static String user = "neo4j";
    final static String password = "root";

    public static void main(String[] args) {
//        PrintRtree();
//        PrintBtree();
//        neo4jDriverInsert();
        neo4jDriverQuery();


    }


    public static void PrintRtree(){
        String file = Data_file.open_file(fileName);
        RTree<Integer, Point> rTree = CreateNewIndex.createRtree_uid(JSONArray.parseArray(file));

        System.out.println(rTree.asString());

    }
    
    public static void PrintBtree(){
        String file = Data_file.open_file(fileName);
        BPlusTree<LocalDateTime,Integer> bTree = CreateNewIndex.createBtree_uid(JSONArray.parseArray(file));


        bTree.print();

    }

    public static void neo4jDriverInsert(){
        Driver driver; //driver instance

        String file = Data_file.open_file(fileName);

        JSONArray array = JSONArray.parseArray(file);

        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        try (Session session = driver.session()) {
            // Wrapping a Cypher Query in a Managed Transaction provides atomicity
            // and makes handling errors much easier.
            // Use `session.writeTransaction` for writes and `session.readTransaction` for reading data.
            // These methods are also able to handle connection problems and transient errors using an automatic retry mechanism.

            for (int i =0;i<array.size();i++) {
                JSONObject object = array.getJSONObject(i);
                session.writeTransaction(tx -> tx.run("MERGE (a:Info {col: $col,row:$row,datetime:$datetime,key:$key})", parameters("col", object.getDouble("col"),"row",object.getDouble("row"),"datetime",object.get("datetime").toString(),"key",object.getString("key"))));
            }
        }
        driver.close();
    }

    public static void neo4jDriverQuery(){

        Driver driver; //driver instance

        ArrayList<Integer> testRS = new ArrayList<>();
        for (int i =10000;i<13000;i++){
            testRS.add(i);
        }

        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));


        Long start = System.currentTimeMillis();

        JSONArray rs = new JSONArray();

        try (Session session = driver.session())
        {
            // A Managed transaction is a quick and easy way to wrap a Cypher Query.
            // The `session.run` method will run the specified Query.
            // This simpler method does not use any automatic retry mechanism.

            //MATCH (a:Person) WHERE a.name STARTS WITH $x RETURN a.name AS name
            Result result = session.run(
                    "MATCH (r) WHERE id(r) IN $array RETURN r.col AS col,r.row AS row,r.key AS key,r.datetime AS datetime",

                    parameters("array", testRS.toArray()));


            // Each Cypher execution returns a stream of records.
            while (result.hasNext())
            {
                Record record = result.next();
                // Values can be extracted from a record by index or name.
                JSONObject object = new JSONObject();
                object.put("col",record.get("col"));
                object.put("row",record.get("row"));
                object.put("datetime",record.get("datetime"));
                object.put("key",object.get("key"));

                rs.add(object);
            }
        }

        System.out.println("查询3000个neo4j时间为："+(System.currentTimeMillis()-start)+"ms");

        driver.close();
    }


}
