package array;

import math.bits.BitsOperator;
import util.Pair;

public class U16 extends Base {

    public U16() {
        super();
    }

    // Get returns value at "idx" and a bool indicating if the value is
// found.
//
// Since 0.2.0
    @Override
    public Pair<Character, Boolean> get(int idx) {
//        System.out.println("start U16 get: idx=" + idx);
        Pair<Integer, Integer> tmp = bmBit(idx);

        int iBm = tmp.left;
        int iBit = tmp.right;
        Long n = array32.getBitmaps(iBm);

        if (((n >> iBit) & 1) == 0) {
            return new Pair((char) 0, false);
        }

        int cnt1 = BitsOperator.onesCount64(n & ((1L << iBit) - 1));

        int stIdx = array32.getOffsets(iBm) * 2 + cnt1 * 2;

        Pair<Character, Boolean> result = new Pair<Character, Boolean>(endian.Uint16(array32.getElts().substring(stIdx).toByteArray()), true);
//        System.out.println("End U16 get: result=" + result);
        return result;
    }
}
