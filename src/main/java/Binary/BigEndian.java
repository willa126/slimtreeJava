package Binary;

public class BigEndian implements ByteOrder {
    //unint16
    public char Uint16(byte[] b) {
//        _ = b[1] // bounds check hint to compiler; see golang.org/issue/14808
        return (char)((char)b[1] | (char)(b[0]<<(char)8));
    }

    public void PutUint16(byte[] b, char v) {
//        _ = b[1] // early bounds check to guarantee safety of writes below
        b[0] = (byte)(v >> 8);
        b[1] = (byte)(v);
    }

    public int Uint32(byte[] b) {
//        _ = b[3] // bounds check hint to compiler; see golang.org/issue/14808
        return b[3] | b[2]<<8 | b[1]<<16 | b[0]<<24;
    }

    public void PutUint32(byte[] b, int v) {
//        _ = b[3] // early bounds check to guarantee safety of writes below
        b[0] = (byte)(v >>24);
        b[1] = (byte)(v >> 16);
        b[2] = (byte)(v >> 8);
        b[3] = (byte)(v);
    }

    //uint64
    public long Uint64(byte[] b) {
//        _ = b[7] // bounds check hint to compiler; see golang.org/issue/14808
        return (long)b[7] | (long)b[6]<<8 | (long)b[5]<<16 | (long)(b[4])<<24 |
                (long)(b[3])<<32 | (long)(b[2])<<40 | (long)(b[1])<<48 | (long)(b[0])<<56;
    }

    public void PutUint64(byte[] b, long v ) {
//        _ = b[7] // early bounds check to guarantee safety of writes below
        b[0] = (byte)(v >> 56);
        b[1] = (byte)(v >> 48);
        b[2] = (byte)(v >> 40);
        b[3] = (byte)(v >> 32);
        b[4] = (byte)(v >> 24);
        b[5] = (byte)(v >> 16);
        b[6] = (byte)(v >> 8);
        b[7] = (byte)(v);
    }

    public String String() { return "BigEndian"; }

    public String GoString() { return "binary.BigEndian"; }
}
