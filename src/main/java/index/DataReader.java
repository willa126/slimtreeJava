package index;

import util.Pair;

public interface DataReader {
    Pair<String, Boolean> read(long offset, String key);
}
