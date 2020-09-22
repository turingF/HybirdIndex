package IndexTestWithId;

import Operation.ProduceTestData;

public class TestData {
    public static void main(String[] args) {

        int endTime = 2012;
        int endLoc = 20;
        ProduceTestData factory = new ProduceTestData(10000,"./src/testData/myNode.json");
        factory.produce();

    }
}
