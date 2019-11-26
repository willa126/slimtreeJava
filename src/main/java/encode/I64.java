package encode;

import Binary.LittleEndian;
import util.Pair;

// I64 converts int64 to slice of 8 bytes and back.
public class I64 <T> implements Encoder<T>{

    // Encode converts int64 to slice of 8 bytes.
    public byte[] encode(T d) {
        byte[] bytes = new byte[8];
//        v := uint64(d.(int64))
        LittleEndian littleEndian = new LittleEndian();
        littleEndian.PutUint64(bytes, (Long) d);
        return bytes;
    }

    // Decode converts slice of 8 bytes to int64.
    // It returns number bytes consumed and an int64.
    public Pair<Integer, T> decode(byte[] b) {
        LittleEndian littleEndian = new LittleEndian();
        return new Pair(8, littleEndian.Uint64(b));
    }

    // getSize returns the size in byte after encoding v.
    public int getSize(String ss){
        return 8;
    }

    // getEncodedSize returns 8.
    public int getEncodedSize(byte[] b) {
        return 8;
    }
}
