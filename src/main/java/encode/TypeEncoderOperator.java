package encode;

import Binary.BinaryOperator;
import Binary.ByteOrder;

public class TypeEncoderOperator<T> {

    // defaultEndian is default endian
    public ByteOrder defaultEndian = BinaryOperator.littleEndian;

    // NewTypeEncoder creates a *TypeEncoder by a value.
    // The value "zero" defines what type this Encoder can deal with and must be a
    // fixed size type.
    // zero interface{}
    public TypeEncoder newTypeEncoder(T zero) throws Exception {
        return newTypeEncoderEndian(zero, null);
    }

    // NewTypeEncoderEndian creates a *TypeEncoder with a specified byte order.
    // "endian" could be binary.LittleEndian or binary.BigEndian.
    public TypeEncoder newTypeEncoderEndian(T zero, ByteOrder endian) throws Exception {
        if (endian == null) {
            endian = defaultEndian;
        }
        TypeEncoder m = new TypeEncoder(endian, getTypeSize(zero));

        if (m.Size == -1) {
//            return nil, errors.Wrapf(ErrNotFixedSize, "type: %v", reflect.TypeOf(zero))
            throw new Exception("ErrNotFixedSize");
        }

        return m;
    }

    private int getTypeSize(T type){
        if(type instanceof Character){
            return 2;
        } else if(type instanceof Integer){
            return 4;
        } else if(type instanceof Long){
            return 8;
        }

        return 0;
    }
}
