package index;

import encode.I64;
import trie.SlimTrie;
import trie.SlimtrieOperator;

import java.util.List;

public class SlimIndexOperator {

    // NewSlimIndex creates SlimIndex instance.
    //
    // The keys in `index` must be in ascending order.
    public SlimIndex newSlimIndex(List<OffsetIndexItem> index, DataReader dr) throws Exception{

        int l = index.size();
        String[]  keys = new String[l];
        long[] offsets = new long[l];

        for(int i = 0; i < l; i++ ){
            keys[i] = index.get(i).key;
            offsets[i] = index.get(i).offset;
        }

        SlimtrieOperator slimtrieOperator = new SlimtrieOperator();
        SlimTrie slimTrie = slimtrieOperator.newSlimTrie(new I64(), keys, offsets);

        return new SlimIndex(slimTrie, dr);
    }
}
