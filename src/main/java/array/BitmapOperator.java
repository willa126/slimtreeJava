package array;

import math.bits.BitsOperator;
import proto.Array;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

public class BitmapOperator {//implements Bitmap{

    // BitsFlagDenseRank

    public static final int BitsFlagDenseRank = 0x00000001;

    // NewBitsJoin creates a new Bitmapper instance from a serias of sub bitmap.
    //
    // Since 0.5.4

    /**
     * @param elts
     * @param eltWidth 16
     * @param dense    false
     * @return
     * @throws Exception
     */
    public Array.Bits newBitsJoin(List<Long> elts, int eltWidth, boolean dense) throws Exception {
//        System.out.println("start newBitsJoin: elts.size=" + elts.size() + ", eltWidth=" + eltWidth + ",dense=" + dense);
        Pair<Integer, List<Long>> pair = concatBits(elts, eltWidth);
        List<Long> words = pair.right;
        List<Integer> index = newRankIndex2(words);
        Array.Bits.Builder bitsBuilder = Array.Bits.newBuilder();
        bitsBuilder.setFlags(0)
                .setN(pair.left)
                .addAllWords(words);

        if (dense) {
            bitsBuilder.setFlags(bitsBuilder.getFlags() | BitsFlagDenseRank);
            PolyArrayOperator polyArrayOperator = new PolyArrayOperator();
            bitsBuilder.setRankIndexDense(polyArrayOperator.newPolyArray(index));
        } else {
            bitsBuilder.clearRankIndex().addAllRankIndex(index);
        }

        return bitsBuilder.build();
    }

    /**
     * @param elts
     * @param width 16
     * @return (int32, []uint64)
     */
    public Pair<Integer, List<Long>> concatBits(List<Long> elts, int width) {
//        System.out.println("start concatBits: elts.size=" + elts.size() + ", width=" + width);
        int wcap = 64 / width; //4
        int l = elts.size();

        int nWords = (l + wcap - 1) / wcap;
        List<Long> words = new ArrayList<Long>(nWords);
        for (int i = 0; i < nWords; i++) {
            words.add(0L);
        }
        // put these 16bits elts into 64bits words
        for (int i = 0; i < elts.size(); i++) {
            int iWord = i / wcap;
            int tmpI = i % wcap;
            words.set(iWord, words.get(iWord) | (elts.get(i) << (tmpI * width)));
        }

        if (words.size() == 0) {
            return new Pair(0, words);
        }

        long last = words.get(words.size() - 1);
        int n = (nWords * 64) - BitsOperator.leadingZeros64(last);
        return new Pair(n, words);
    }


    public List<Integer> newRankIndex1(List<Long> words) { //[]int32
//        System.out.println("start newRankIndex1: words.size=" + words.size());
        // One uint64 words share one index
        List<Integer> idx = new ArrayList<Integer>();
        int n = 0;
        for (int i = 0; i < words.size(); i++) {
            idx.add(n);
            n += BitsOperator.onesCount64(words.get(i));
        }
        //len(idx)=len(words)
//        System.out.println("end newRankIndex1: idx size=" + idx.size());
        return idx;
    }

    public List<Integer> newRankIndex2(List<Long> words) {
//        System.out.println("start newRankIndex2: words size =" + words.size());

        // two uint64 words share one index
        List<Integer> idx = new ArrayList<Integer>();
        int n = 0;
        for (int i = 0; i < words.size(); i += 2) {
            idx.add(n);
            n += BitsOperator.onesCount64(words.get(i));
            if (i < words.size() - 1) {
                n += BitsOperator.onesCount64(words.get(i + 1));
            }
        }

        // Need a last index to let distance from every bit to its closest index
        if ((words.size() & 1) == 0) {
            idx.add(n);
        }

        return idx;
    }

}
