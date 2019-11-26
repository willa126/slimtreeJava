package trie;

public class BitWord {
    int width;
    int byteCap;
    byte wordMask;

    BitWord(int width) {
        this.width = width;
        byteCap = 8 / width;
        wordMask = (byte) ((1 << width) - 1);
    }

    public int firstDiff(String a, String b, int from, int end) {
//        System.out.println("start firstDiff: a=" + a + ";b=" + b + "; byteCap=" + byteCap);
        int la = a.length() * byteCap;
        int lb = b.length() * byteCap;

        if (end == -1) {
            end = la;
        }

        if (end > la) {
            end = la;
        }

        if (end > lb) {
            end = lb;
        }

        for (int i = from; i < end; i++) {
            if (get(a, i) != get(b, i)) {
                return i;
            }
        }
        return end;
    }

    public byte get(String s, int ith) {
        int i = width * ith;
        int end = (i + width - 1) & 7;

        byte word = (byte) s.charAt(i >> 3);
        byte result = (byte) ((word >> (7 - end)) & wordMask);
//        System.out.println("get in BitWord: s=" + s + ", ith=" + ith + ", result=" + result);
        return result;
    }
}
