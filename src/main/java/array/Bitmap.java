package array;

import java.util.Map;

public interface Bitmap {
    Map<String, Integer> stat();

    boolean has(int x);

    int len();

    long bits();

    int rank(int x);
}
