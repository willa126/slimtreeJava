package encode;

import util.Pair;

// A Encoder converts one element between serialized byte stream
// and in-memory data structure.
public interface Encoder<T> {
    // Convert into serialized byte stream.
    byte[] encode(T s);

    // Read byte stream and convert it back to typed data.
    Pair<Integer, T> decode(byte[] b);

    // getSize returns the size in byte after encoding v.
    // If v is of type this encoder can not encode, it panics.
    int getSize(String v);

    // getEncodedSize returns size of the encoded value.
    // Encoded element may be var-length.
    // This function is used to determine element size without the need of
    // encoding it.
    int getEncodedSize(byte[] b);
}
