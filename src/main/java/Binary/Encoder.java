package Binary;

public class Encoder extends Coder {
    public void bool( boolean x) {
        if(x) {
            buf[offset] = 1;
        } else {
            buf[offset] = 0;
        }
        offset++;
    }

    public void uint8(byte x) {
        buf[offset] = x;
        offset++;
    }

    public void uint16(char x) {
        order.PutUint16(cloneArray(buf, offset, offset+2), x);
        offset += 2;
    }

    public void uint32(int x) {
        order.PutUint32(cloneArray(buf, offset, offset+4), x);
        offset += 4;
    }

    public void uint64(long x) {
        order.PutUint64(cloneArray(buf, offset, offset+8), x);
        offset += 8;
    }

    public void int8(byte x){
        uint8(x);
    }

    public void int16(char x){
        uint16(x);
    }

    public void int32(int x){
        uint32(x);
    }

    public void int64(long x){
        uint64(x);
    }

}
