package trie;

import array.Array;
import array.BaseOperator;
import array.Bitmap16;
import array.U16;
import encode.Encoder;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

public class SlimtrieOperator {
    public static BitWord bitWord4 = new BitWord(4);

    public SlimTrie slimTrie = new SlimTrie();

    public static int maxNodeCnt = (1 << 31) - 1;

    public SlimTrie newSlimTrie(Encoder encoder, String[] keys, long[] values) throws Exception {
//        System.out.println("start newSlimTrie: keys.size=" + keys.length + ", values.size=" + values.length);
        int n = keys.length;
        if (n == 0) {
//            System.out.println("keys.length=0");
            return new SlimTrie(encoder);
        }

        for (int i = 0; i < keys.length - 1; i++) {
            if (keys[i].compareTo(keys[i + 1]) >= 0) {
                throw new Exception(String.format("keys[%d] >= keys[%d] %s %s", i, i + 1, keys[i], keys[i + 1]));
//                return nil, errors.Wrapf(ErrKeyOutOfOrder,
            }
        }

        boolean[] toKeep = newValueToKeep(values);

        List<Integer> childi = new ArrayList(n); //int32
        List<Long> childv = new ArrayList(n); //uint64

        List<Integer> stepi = new ArrayList(n); //int32
        List<Character> stepv = new ArrayList(n); //uint16

        List<Integer> leavesi = new ArrayList(n);//int32
        List<Long> leavesv = new ArrayList(n);

        List<Subset> queue = new ArrayList(n * 2);
        queue.add(new Subset(0, n, 0));

        System.out.println("begin to construct trie");
        for (int i = 0; i < queue.size(); i++) {
            int nid = i; //int32
            Subset o = queue.get(i);
            int s = o.keyStart;
            int e = o.keyEnd;  //nid=0;s=0;e=n
//            System.out.println("nid=" + nid + ", s=" + s + ", e=" + e + ", from=" + o.fromIndex);

            // single key, it is a leaf
            if (e - s == 1) {
                if (toKeep[s]) {
                    leavesi.add(nid);
                    leavesv.add(values[s]);
//                    System.out.println("find leaf: s=" + s + ", e=" + e + ", nid=" + nid + ", values[s]=" + values[s]);
                }
                continue;
            }

            // need to create an inner node
            String[] keysClone = getCloneArray(keys, s, e);
            int prefI = prefixIndex(keysClone, o.fromIndex);
//            System.out.println("prefI=" + prefI);
            // the first key is a prefix of all other keys, which makes it a leaf.
            boolean isFirstKeyALeaf;
            isFirstKeyALeaf = keys[s].length() * 8 / 4 == prefI;
            if (isFirstKeyALeaf) {
                if (toKeep[s]) {
                    leavesi.add(nid);
                    leavesv.add(values[s]);
//                    System.out.println("find leaf: s=" + s + ", nid=" + nid + ", values[s]=" + values[s]);
                }
                s++;
            }

//            System.out.println("start to getLabels for prefI " + prefI);
            // create inner node from following keys
            Pair<List<Byte>, Character> pair = getLabels(getCloneArray(keys, s, e), prefI,
                    getCloneArray(toKeep, s, e));
            List<Byte> labels = pair.left;
            Character labelBitmap = pair.right;

            boolean hasChildren = labels.size() > 0;

//            System.out.println(" nid " + nid + " hasChildren " + hasChildren);
            if (hasChildren) {
                childi.add(nid);
                childv.add((long) labelBitmap); //uint64(labelBitmap)

                // put keys with the same starting word to queue.
                for (Byte label : labels) {
                    // Find the first key starting with label
                    for (; s < e; s++) {
                        byte word = bitWord4.get(keys[s], prefI);
                        if (word == label) {
                            break;
                        }
                    }

                    // Continue looking for the first key not starting with label
                    int j;
                    for (j = s + 1; j < e; j++) {
                        byte word = bitWord4.get(keys[j], prefI);
                        if (word != label) {
                            break;
                        }
                    }

                    Subset p = new Subset(s, j, prefI + 1/*skip the label word*/);
                    queue.add(p);
                    s = j;
                }

                // 1 for the label word at parent node
                int step = (prefI - o.fromIndex) + 1;
                if (step > 0xffff) {
                    System.out.println(String.format("step=%d is too large. must < 2^16", step));
                }

                // By default to move 1 step forward, thus no need to store 1
                boolean hasStep = step > 1;
                if (hasStep) {
                    stepi.add(nid);
                    stepv.add((char) step);
                }
            }
        }

        System.out.println("start to build three array");

        int nodeCnt = queue.size();
        BaseOperator baseOperator = new BaseOperator();
        Bitmap16 ch = baseOperator.newBitmap16(childi, childv, 16);
        System.out.println("Successfully newBitmap16");

        U16 steps = baseOperator.newU16(stepi, stepv);
        System.out.println("Successfully newU16");

        Array leaves = new Array();
        leaves.EltEncoder = encoder;

        leaves.init(leavesi, leavesv);
        System.out.println("Successfully init leaves");

        // Avoid panic of slice index out of bound. yl_todo
        ch.extendIndex(nodeCnt);
        steps.extendIndex(nodeCnt);
        leaves.extendIndex(nodeCnt);

        SlimTrie st = new SlimTrie(ch, steps, leaves);

        return st;
    }

    public SlimTrie emptySlimTrie(encode.Encoder e) {
        SlimTrie slimTrie = new SlimTrie();
        slimTrie.leaves.EltEncoder = e;
        return slimTrie;
    }

    public String[] getCloneArray(String[] array, int s, int e) {
//        System.out.print("getCloneArray:");
        int size = e - s;
        String[] arrayClone = new String[size];
        for (int j = 0; j < size; j++) {
            arrayClone[j] = array[j + s];
//            System.out.print(arrayClone[j] + " ");
        }
//        System.out.println("");
//        System.out.println("\n end of getCloneArray");
        return arrayClone;
    }

    public boolean[] getCloneArray(boolean[] array, int s, int e) {
        boolean[] arrayClone = new boolean[e - s];
        for (int j = 0; j < e - s; j++) {
            arrayClone[j] = array[j + s];
        }
        return arrayClone;
    }

    public boolean[] newValueToKeep(long[] values) {
        int n = values.length;

        boolean[] toKeep = new boolean[n];
        toKeep[0] = true;

        for (int i = 0; i < n - 1; i++) {
            toKeep[i + 1] = values[i + 1] != values[i];
        }
        return toKeep;
    }

    public int prefixIndex(String[] keys, int from) {
//        System.out.println("start prefixIndex: keys.size=" + keys.length + ", from=" + from);
        if (keys.length == 1) {
            return keys[0].length();
        }

        int n = keys.length;
//        System.out.println("prefixIndex n =" + n);
        int end = bitWord4.firstDiff(keys[0], keys[n - 1], from, -1);
        return end;
    }

    //return ([]byte, uint16)
    public Pair<List<Byte>, Character> getLabels(String[] keys, int from, boolean[] toKeep) {
//        System.out.print("getLabels: ");
        List<Byte> labels = new ArrayList(1 << 4);
        char bitmap = 0;

        for (int i = 0; i < keys.length; i++) {
            if (!toKeep[i]) {
                continue;
            }

            byte word = bitWord4.get(keys[i], from);//0000word
            char b = (char) ((char) 1 << word);
//            System.out.println("word="+ word);
//            System.out.println("b="+ (int)b);
            if ((bitmap & b) == 0) {
                labels.add(word);
//                System.out.print(word + " ");
                bitmap |= b;
            }

        }
//        System.out.println(", bitmap=" + (int) bitmap);
        return new Pair(labels, bitmap);
    }
}
