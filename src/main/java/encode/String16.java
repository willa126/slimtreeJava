package encode;

import util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class String16<T> implements Encoder<T> {

    // Encode converts uint16 to slice of 2 bytes.
    public byte[] encode(T d) {
        String ss = (String)d;
        int l = ss.length();
        byte[] rst = new byte[2+l];
        rst[0] = (byte)(l >> 8);
        rst[1] = (byte)(l);
//        rst.addAll(new ArrayList(Arrays.asList(ss.getBytes()))); //yuan_todo
        return rst;
    }

    // Decode converts slice of 2 bytes to uint16.
    // It returns number bytes consumed and an uint16.
    // return (int, interface{})
    public Pair<Integer, T> decode(byte[] b) {
        int l = b[0]<<8 + b[1];
        StringBuilder stringBuilder = new StringBuilder();
        for( int i=2;i<=2+l;i++){
            stringBuilder.append(b[i]);
        }
        String ss = stringBuilder.toString();
        return new Pair(2 + l, ss);
    }

    // getSize returns number of byte required to encode a string.
    // It is len(str) + 2;
    public int getSize(String ss){
        int l = ss.length();
        return 2 + l;
    }

    // getEncodedSize returned size of encoded data.
    public int getEncodedSize(byte[] b) {
        int l = b[0]<<8 + b[1];
        return 2 + l;
    }
}
