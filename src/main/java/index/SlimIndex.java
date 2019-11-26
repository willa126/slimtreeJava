package index;

import trie.SlimTrie;
import util.Pair;

public class SlimIndex {
    public SlimTrie slimTrie;
    public DataReader dataReader;

    public SlimIndex(SlimTrie slimTrie, DataReader dataReader){
        this.slimTrie = slimTrie;
        this.dataReader = dataReader;
    }


    // Get returns the value of `key` which is found by `SlimIndex.DataReader`, and
    // a bool value indicating if the `key` is found or not.
    public Pair<String, Boolean> get(String key) {
        Pair<Long, Boolean> found = slimTrie.get(key);
        long offset = found.left;
        boolean isFound = found.right;
        System.out.println("offset:"+offset+" isFound:"+isFound);
        return dataReader.read(offset, key);
    }
}
