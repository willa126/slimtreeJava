package array;

import math.bits.BitsOperator;
import util.Triple;

public class Bitmap16 extends Base {

    public Bitmap16() {
        super();
    }

    /**
     * @param idx node id
     * @return
     */
    public Triple<Long, Integer, Boolean> getWithRank(int idx) {
//        System.out.println("getWithRank idx=" + idx);
        int iBm = idx >> bmShift;
        int iBit = idx & bmMask;

        long bm = array32.getBitmaps(iBm);

        if (((bm >> iBit) & 1) == 0) {
//            System.out.println("find false");
            return new Triple(0, 0, false);
        }

//        System.out.println("iBm=" + iBm + " bm=" + bm + "  iBit=" + iBit + "  offsets.size=" + array32.getOffsetsCount());
        int cnt1 = BitsOperator.onesCount64(bm & ((1L << iBit) - 1));

        int eltBitIdx = (array32.getOffsets(iBm) + cnt1) << 4;
        int iWord = eltBitIdx >> 6;
        int j = eltBitIdx & 63;

//        System.out.println("iWord=" + iWord + "  j=" + j);
        long w = array32.getBMElts().getWords(iWord);

        long v = (w >> j) & 0xffff;

        // Bitmap16 does not use dense mode rank index
        // Bitmap16 does not use EltWidth

        int rank = array32.getBMElts().getRankIndex((eltBitIdx + 64) >> 7);
//        System.out.println("new rank=" + rank);
        if ((iWord & 1) == 0) {
            long word = w << (64 - j);
            rank += BitsOperator.onesCount64(word);
        } else {
            long word = w >> j;
            rank -= BitsOperator.onesCount64(word);
        }

        return new Triple(v, rank, true);
    }

}
