package encode;

import Binary.ByteOrder;
import util.Pair;

public class TypeEncoder<T> implements Encoder<T>{

    public ByteOrder Endian;
//     Type is the data type to encode.
//    Type reflect.Type
//    public T Type;
    public int Size;

    TypeEncoder(ByteOrder endian, int Size ){
        this.Endian = endian;
//        this.Type = type;
        this.Size = Size;
    }

    // Encode converts a m.Type value to byte slice.
    // If a different type value from the one used with NewTypeEncoder passed in,
    // it panics.
    public byte[] encode(T d) {
        /*if reflect.Indirect(reflect.ValueOf(d)).Type() != m.Type {
            panic("different type from TypeEncoder.Type")
        }*/
        byte[] bytes = new byte[Size];
        if(Size == 2){
            Endian.PutUint16(bytes, (Character) d);
        } else if(Size == 4){
            Endian.PutUint32(bytes, (Integer) d);
        } else if(Size == 8){
            Endian.PutUint64(bytes, (Long) d);
        }
        return bytes;
    }

    // Decode converts byte slice to a pointer to Type value.
    // It returns number bytes consumed and an Type value in interface{}.
    public Pair<Integer, T> decode(byte[] b) {
        if( Size == 2){
            return new Pair(Size, Endian.Uint16(b));
        } else if(Size == 4){
            return new Pair(Size, Endian.Uint32(b));
        } else if(Size == 8){
            return new Pair(Size, Endian.Uint64(b));
        }
        return null;
    }

    // getSize returns m.Size.
    public int getSize(String ss){
        return Size;
    }

    // getEncodedSize returns m.Size.
    public int getEncodedSize(byte[] b) {
        return Size;
    }
}
