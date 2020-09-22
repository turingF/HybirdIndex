package IndexTestWithId.uidTest;

import Operation.ProduceTestData;

public class produceData {
    public static void main(String[] args) {
        ProduceTestData factory = new ProduceTestData(10000000,"./src/test/uidTest/uid2(20-2015-10w).json");
        factory.produce();
    }
}
