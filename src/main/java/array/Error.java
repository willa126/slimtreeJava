package array;

public class Error {
    // ErrIndexNotAscending indicates that the indexes to initialize an Array is not
    // in ascending order.
    //
    // Since 0.2.0
    public static Exception ErrIndexNotAscending = new Exception("index must be an ascending ordered slice");

    // ErrIndexLen indicates that the number of indexes does not equal the number of
    // elements, when initializing an Array.
    //
    // Since 0.2.0
    public static Exception ErrIndexLen = new Exception("the length of indexes and elts must be equal");
}
