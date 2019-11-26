package Binary;

public class Decoder extends Coder {
    public boolean bool() {
        int x = buf[offset];
        offset++;
        return x != 0;
    }

    public byte uint8() {
        byte x = buf[offset];
        offset++;
        return x;
    }

    public char uint16() {
        char x = order.Uint16(cloneArray(buf, offset, offset + 2));
        offset += 2;
        return x;
    }

    public int uint32() {
        int x = order.Uint32(cloneArray(buf, offset, offset + 4));
        offset += 4;
        return x;
    }

    public long uint64() {
        long x = order.Uint64(cloneArray(buf, offset, offset + 8));
        offset += 8;
        return x;
    }

    public byte int8() {
        return uint8();
    }

    public char int16() {
        return uint16();
    }

    public int int32() {
        return uint32();
    }

    public long int64() {
        return uint64();
    }

    /*func (d *decoder) value(v reflect.Value) {
        switch v.Kind() {
            case reflect.Array:
                l := v.Len()
                for i := 0; i < l; i++ {
                d.value(v.IndexOperator(i))
            }

            case reflect.Struct:
                t := v.Type()
                l := v.NumField()
                for i := 0; i < l; i++ {
                // Note: Calling v.CanSet() below is an optimization.
                // It would be sufficient to check the field name,
                // but creating the StructField info for each field is
                // costly (run "go test -bench=ReadStruct" and compare
                // results when making changes to this code).
                if v := v.Field(i); v.CanSet() || t.Field(i).Name != "_" {
                    d.value(v)
                } else {
                    d.skip(v)
                }
            }

            case reflect.Slice:
                l := v.Len()
                for i := 0; i < l; i++ {
                d.value(v.IndexOperator(i))
            }

            case reflect.Bool:
                v.SetBool(d.bool())

            case reflect.Int8:
                v.SetInt(int64(d.int8()))
            case reflect.Int16:
                v.SetInt(int64(d.int16()))
            case reflect.Int32:
                v.SetInt(int64(d.int32()))
            case reflect.Int64:
                v.SetInt(d.int64())

            case reflect.Uint8:
                v.SetUint(uint64(d.uint8()))
            case reflect.Uint16:
                v.SetUint(uint64(d.uint16()))
            case reflect.Uint32:
                v.SetUint(uint64(d.uint32()))
            case reflect.Uint64:
                v.SetUint(d.uint64())

            case reflect.Float32:
                v.SetFloat(float64(math.Float32frombits(d.uint32())))
            case reflect.Float64:
                v.SetFloat(math.Float64frombits(d.uint64()))

            case reflect.Complex64:
                v.SetComplex(complex(
                        float64(math.Float32frombits(d.uint32())),
                        float64(math.Float32frombits(d.uint32())),
                        ))
            case reflect.Complex128:
                v.SetComplex(complex(
                        math.Float64frombits(d.uint64()),
                        math.Float64frombits(d.uint64()),
                        ))
        }
    }*/
}
