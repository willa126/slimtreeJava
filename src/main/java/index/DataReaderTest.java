package index;

import util.Pair;

import java.util.Arrays;
import java.util.List;

public class DataReaderTest implements DataReader{
    String data; //"Aaron,1,Agatha,1,Al,2,Albert,3,Alexander,5,Alison,8"
    public DataReaderTest(String data){
        this.data = data;
    }

    /**
     *
     * @param offset  0
     * @param key Aaron
     * @return
     */
    public Pair<String, Boolean> read(long offset, String key){
        String subkey = data.substring((int)offset);
        String[] subArray = subkey.split(",");
        List<String> result = Arrays.asList(subArray).subList(0,2);
        if(result.get(0).equals(key)){
            return new Pair(result.get(1), true);
        }
        return new Pair("", false);
    }

}
