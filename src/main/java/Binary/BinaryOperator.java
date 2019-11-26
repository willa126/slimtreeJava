package Binary;

import java.util.ArrayList;
import java.util.List;

public class BinaryOperator {
    // LittleEndian is the little-endian implementation of ByteOrder.
    public static LittleEndian littleEndian = new LittleEndian();

    // BigEndian is the big-endian implementation of ByteOrder.
    public static BigEndian bigEndian = new BigEndian();


    /*// Write writes the binary representation of data into w.
    // Data must be a fixed-size value or a slice of fixed-size
    // values, or a pointer to such data.
    // Boolean values encode as one byte: 1 for true, and 0 for false.
    // Bytes written to w are encoded using the specified byte order
    // and read from successive fields of the data.
    // When writing structs, zero values are written for fields
    // with blank (_) field names.
    public void Write(io.Writer w, ByteOrder order, String data) {
        // Fast path for basic types and slices.
        int n = intDataSize(data);
        List<Byte> bs;
        if(n != 0) {
            bs = new ArrayList(n);
        }


        switch v := data.(type) {
		case *bool:
            if *v {
                bs[0] = 1
            } else {
                bs[0] = 0
            }
            case bool:
                if v {
                bs[0] = 1
            } else {
                bs[0] = 0
            }
		case []bool:
            for i, x := range v {
                if x {
                    bs[i] = 1
                } else {
                    bs[i] = 0
                }
            }
		case *int8:
            bs[0] = byte(*v)
            case int8:
                bs[0] = byte(v)
		case []int8:
            for i, x := range v {
                bs[i] = byte(x)
            }
		case *uint8:
            bs[0] = *v
            case uint8:
                bs[0] = v
		case []uint8:
            bs = v // TODO(josharian): avoid allocating bs in this case?
		case *int16:
            order.PutUint16(bs, uint16(*v))
            case int16:
                order.PutUint16(bs, uint16(v))
		case []int16:
            for i, x := range v {
                order.PutUint16(bs[2*i:], uint16(x))
            }
		case *uint16:
            order.PutUint16(bs, *v)
            case uint16:
                order.PutUint16(bs, v)
		case []uint16:
            for i, x := range v {
                order.PutUint16(bs[2*i:], x)
            }
		case *int32:
            order.PutUint32(bs, uint32(*v))
            case int32:
                order.PutUint32(bs, uint32(v))
		case []int32:
            for i, x := range v {
                order.PutUint32(bs[4*i:], uint32(x))
            }
		case *uint32:
            order.PutUint32(bs, *v)
            case uint32:
                order.PutUint32(bs, v)
		case []uint32:
            for i, x := range v {
                order.PutUint32(bs[4*i:], x)
            }
		case *int64:
            order.PutUint64(bs, uint64(*v))
            case int64:
                order.PutUint64(bs, uint64(v))
		case []int64:
            for i, x := range v {
                order.PutUint64(bs[8*i:], uint64(x))
            }
		case *uint64:
            order.PutUint64(bs, *v)
            case uint64:
                order.PutUint64(bs, v)
		case []uint64:
            for i, x := range v {
                order.PutUint64(bs[8*i:], x)
            }
		}
            _, err := w.Write(bs)
            return err
        }

        // Fallback to reflect-based encoding.
        v := reflect.Indirect(reflect.ValueOf(data))
        size := dataSize(v)
        if size < 0 {
            return errors.New("binary.Write: invalid type " + reflect.TypeOf(data).String())
        }
        buf := make([]byte, size)
        e := &encoder{order: order, buf: buf}
        e.value(v)
        _, err := w.Write(buf)
        return err
    }*/

}
