package array;

import encode.TypeEncoder;
import encode.TypeEncoderOperator;

import java.util.List;

public class Array<T> extends Base <T>{

    // Init initializes an Array.
    // Length of "indexes" and length of "elts" must be the same.
    // "elts" must be a slice of fixed-size values.
    //
    // By default Array encodes an element with binary.Write(), in binary.LittenEndian.
    //
    // Since 0.2.0
//    @Override
    public void init(List<Integer> indexes, List<T> elts)throws Exception  {
        super.init(indexes, elts);

        // Only when inited with some elements, we init the Encoder
        if( array32.getCnt() > 0 && EltEncoder == null ){

            T v = elts.get(0);
            TypeEncoderOperator typeEncoderOperator = new TypeEncoderOperator();
            TypeEncoder encoder = typeEncoderOperator.newTypeEncoderEndian(v, endian);

            EltEncoder = encoder;
        }
    }
}
