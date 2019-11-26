package Binary;

public class LittleEndian implements ByteOrder{
    //unint16
    public char Uint16(byte[] b) {
//        _ = b[1] // bounds check hint to compiler; see golang.org/issue/14808
        char c =  (char)((char)b[0] | (char)b[1]<<(char)8);
        return c;
    }

    public void PutUint16(byte[] b, char v) {
//        _ = b[1] // early bounds check to guarantee safety of writes below
        b[0] = (byte)v;
        b[1] = (byte) ((byte)v >> (char)8);
    }

    public int Uint32(byte[] b) {
//        _ = b[3] // bounds check hint to compiler; see golang.org/issue/14808
        return b[0] | b[1]<<8 | b[2]<<16 | b[3]<<24;
    }

    public void PutUint32(byte[] b, int v) {
//        _ = b[3] // early bounds check to guarantee safety of writes below
        b[0] = (byte)v;
        b[1] = (byte)(v >> 8);
        b[2] = (byte)(v >> 16);
        b[3] = (byte)(v >> 24);
    }

    //uint64
    public long Uint64(byte[] b) {
//        _ = b[7] // bounds check hint to compiler; see golang.org/issue/14808
        return (long)b[0] | (long)b[1]<<8 | (long)b[2]<<16 | (long)(b[3])<<24 |
                (long)(b[4])<<32 | (long)(b[5])<<40 | (long)(b[6])<<48 | (long)(b[7])<<56;
    }

    public void PutUint64(byte[] b, long v ) {
//        _ = b[7] // early bounds check to guarantee safety of writes below
        b[0] = (byte)v;
        b[1] = (byte)(v >> 8);
        b[2] = (byte)(v >> 16);
        b[3] = (byte)(v >> 24);
        b[4] = (byte)(v >> 32);
        b[5] = (byte)(v >> 40);
        b[6] = (byte)(v >> 48);
        b[7] = (byte)(v >> 56);
    }

    public String String() { return "LittleEndian"; }

    public String GoString() { return "binary.LittleEndian"; }
}
