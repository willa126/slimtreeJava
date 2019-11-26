package trie;

import array.Array;
import array.Bitmap16;
import array.U16;
import encode.Encoder;
import math.bits.BitsOperator;
import util.Pair;
import util.Triple;

public class SlimTrie {
    public Bitmap16 children = new Bitmap16();
    public U16 steps = new U16();
    public Array leaves = new Array();

    SlimTrie() {

    }

    SlimTrie(Encoder encoder) {
        leaves.EltEncoder = encoder;
    }

    SlimTrie(Bitmap16 ch, U16 steps, Array leaves) {
        this.children = ch;
        this.steps = steps;
        this.leaves = leaves;
    }

    // just return equal value for trie.Search benchmark

    // Get the value of the specified key from SlimTrie.
    //
    // If the key exist in SlimTrie, it returns the correct value.
    // If the key does NOT exist in SlimTrie, it could also return some value.
    //
    // Because SlimTrie is a "index" but not a "kv-map", it does not stores complete
    // info of all keys.
    // SlimTrie tell you "WHERE IT POSSIBLY BE", rather than "IT IS JUST THERE".
    //
    // Since 0.2.0

    /**
     * @param key
     * @return offset&found
     */
    public Pair<Long, Boolean> get(String key) {

        byte word = 0;
        boolean found = false;
        Pair<Long, Boolean> result = new Pair(0L, found);

        int eqID = 0;

        // string to 4-bit words
        int lenWords = key.length() * 2;

        int idx = -1;
        while (true) {
            Triple<Long, Integer, Boolean> tmp = children.getWithRank(eqID);
            long bm = tmp.first;
            int rank = tmp.second;
            boolean hasInner = tmp.third;
//            System.out.println("SlimTrie get: bm=" + bm + ", rank=" + rank + ", hasInner=" + hasInner);
            if (!hasInner) { //does not have children
                // maybe a leaf
                break;
            }

//            System.out.println("begin to get steps for eqID " + eqID);
            Pair<Character, Boolean> stepResult = steps.get(eqID);
            Character step = stepResult.left;
            boolean foundstep = stepResult.right;
            if (foundstep) {
                idx += (int) step;
            } else {
                idx++;
            }

            if (lenWords < idx) {
                eqID = -1;
                break;
            }

            if (lenWords == idx) {
                break;
            }

//            System.out.println("begin to get word for idx " + idx);
            // Get a 4-bit word from 8-bit words.
            // Use arithmetic to avoid branch missing.
            int shift = 4 - (idx & 1) * 4;
            word = (byte) ((key.charAt(idx >> 1) >> shift & 0x0f));

            long bb = 1L << word;
            if ((bm & bb) != 0) {
                int chNum = BitsOperator.onesCount64(bm & (bb - 1));
                eqID = rank + 1 + chNum;//next nodeid
            } else {
                eqID = -1;//no match
                break;
            }
        }

        if (eqID != -1) {
            result = leaves.get(eqID);
        }

        return result;
    }
}
