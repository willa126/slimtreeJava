package Binary;

public interface ByteOrder {

    //unint16
    char Uint16(byte[] b);

    void PutUint16(byte[] b, char v);

    int Uint32(byte[] b);

    void PutUint32(byte[] b, int v);

    //uint64
    long Uint64(byte[] b);

    void PutUint64(byte[] b, long v);

    String String();

    String GoString();

}
