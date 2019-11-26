package Binary;

public class Coder {
    public ByteOrder order;
    public byte[] buf;
    public int offset;

    public byte[] cloneArray(byte[] array, int s, int e) {
        int size = e - s;
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = array[i + s];
        }
        return result;
    }
}
