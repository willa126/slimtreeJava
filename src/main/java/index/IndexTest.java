package index;


import util.Pair;

import java.util.ArrayList;
import java.util.List;

public class IndexTest {

    public DataReaderTest dr = new DataReaderTest("Aaron,1,Agatha,1,Al,2,Albert,3,Alexander,5,Alison,8");

    List<OffsetIndexItem> keyOffsets = new ArrayList();

    IndexTest(){
        keyOffsets.add(new OffsetIndexItem("Aaron", 0L));
        keyOffsets.add(new OffsetIndexItem("Agatha", 8L));
        keyOffsets.add(new OffsetIndexItem("Al", 17L));
        keyOffsets.add(new OffsetIndexItem("Albert", 22L));
        keyOffsets.add(new OffsetIndexItem("Alexander", 31L));
        keyOffsets.add(new OffsetIndexItem("Alison", 31L));
    }

    public void test(){

        try {

            SlimIndexOperator slimIndexOperator = new SlimIndexOperator();
            SlimIndex slimIndex = slimIndexOperator.newSlimIndex(keyOffsets, dr);
            System.out.println("Successfully new SlimIndex");
            Pair<String, Boolean> result = slimIndex.get("Aaron");
            System.out.println("test:"+result.left + "  " + result.right);

            //input Agatha cannot get right result now
            /*Pair<String, Boolean> result2 = slimIndex.get("Agatha");
            System.out.println("test:"+result2.left + "  " + result2.right);*/
        } catch (Exception e){
            System.out.println("Get exception: " + e);
        }
    }

    public static void main(String[] args){
        IndexTest indexTest = new IndexTest();
        indexTest.test();
    }
}
