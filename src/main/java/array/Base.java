package array;

import Binary.BinaryOperator;
import Binary.LittleEndian;
import com.google.protobuf.ByteString;
import encode.Encoder;
import encode.TypeEncoderOperator;
import math.bits.BitsOperator;
import proto.Array;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class Base<T> {
    // bmWidth defines how many bits for a bitmap word
    // Never change this
    public static int bmWidth = 64;
    public static int bmShift = 6;   // log₂64  //unit
    public static int bmMask = 63; //0{26}111111

    public Array.Array32 array32;
    public Encoder EltEncoder;

    // endian is the default endian for array
    public LittleEndian endian = BinaryOperator.littleEndian;

    Base() {
        array32 = Array.Array32.newBuilder().build();
//        EltEncoder = new TypeEncoder();
    }

    // InitIndex initializes index bitmap for an array.
    // IndexOperator must be an ascending int32 slice, otherwise, it return
    // the ErrIndexNotAscending error
    //
    // Since 0.2.0
    public void initIndex(List<Integer> index) throws Exception { //return error
//        System.out.println("start initIndex");

        if (index.size() > 1) {
            for (int i = 0; i < index.size() - 1; i++) {
                if (index.get(i) >= index.get(i + 1)) {
                    throw new Exception("ErrIndexNotAscending");
                }
            }
        }

        if (bmWidth != 64) {
            throw new Exception("newBitmapWords only accept uint64 as bitmap word");
        }

        Array.Array32.Builder builder = array32.toBuilder();
        builder.clearBitmaps().addAllBitmaps(newBitsWords(index).right);
        BitmapOperator bitmapOperator = new BitmapOperator();
        builder.clearOffsets().addAllOffsets(bitmapOperator.newRankIndex1(builder.getBitmapsList()));
        builder.setCnt(index.size());
        array32 = builder.build();
//        System.out.println("array32 offset size=" + array32.getOffsetsCount());
        // Be compatible to previous issue:
        // Since v0.2.0, Offsets is not exactly the same as bitmap ranks.
        // It is 0 for empty bitmap word.
        // But bitmap ranks set rank[i*64] to rank[(i-1)*64] for empty word.
        for (int i = 0; i < array32.getBitmapsCount(); i++) {
            long word = array32.getBitmaps(i);
            if (word == 0) {
                array32 = array32.toBuilder().setOffsets(i, 0).build();
            }
        }
    }

    // param: index []int32
    public Pair<Integer, List<Long>> newBitsWords(List<Integer> nums) {// (int32, []uint64)
//        System.out.println("start newBitsWords: nums.size=" + nums.size());
        int n = 0;
        if (nums.size() > 0) {
            n = nums.get(nums.size() - 1) + 1;
        }

        int nWords = (n + 63) >> 6;
//        System.out.println("nWords=" + nWords);
        List<Long> words = new ArrayList(nWords);//uint64
        for (int i = 0; i < nWords; i++) {
            words.add(0L);
        }

        for (int i = 0; i < nums.size(); i++) {
            int iWord = i >> 6;
            int j = i;
            j = j & 63;
            int tmp = 1 << j;
            words.set(iWord, words.get(iWord) | tmp);
        }

        return new Pair(n, words);
    }

    // ExtendIndex allocaed additional 0-bits after Bitmap and Offset.
    // Since 0.5.9
    public void extendIndex(int n) {
        int nword = (n + 63) >> 6; //1+63=64=1000000 >>6=1111111

        if (nword <= array32.getBitmapsCount()) {
            return;
        }

        List<Long> bitmaps = new ArrayList<Long>(nword);
        bitmaps.addAll(array32.getBitmapsList());

        Array.Array32.Builder builder = array32.toBuilder();
        builder.clearBitmaps().addAllBitmaps(bitmaps);
        BitmapOperator bitmapOperator = new BitmapOperator();
        builder.clearOffsets().addAllOffsets(bitmapOperator.newRankIndex1(array32.getBitmapsList()));
        for (int i = 0; i < builder.getBitmapsList().size(); i++) {
            if (builder.getBitmaps(i) == 0) {
                builder.setOffsets(i, 0);
            }
        }
    }


    // Init initializes an array from the "indexes" and "elts".
    // The indexes must be an ascending int32 slice,
    // otherwise, return the ErrIndexNotAscending error.
    // The "elts" is a slice.
    //
    // Since 0.2.0
    public void init(List<Integer> indexes, List<T> elts) throws Exception {
//        System.out.println("start init: indexes.size=" + indexes + ",elts.size=" + elts.size());
        List<T> rElts = elts;

        int n = rElts.size();
        if (indexes.size() != n) {
            throw Error.ErrIndexLen;
        }

        initIndex(indexes); //nid related bitmap & offset

        if (indexes.size() == 0) {
            return;
        }

        encode.Encoder encoder;

        if (EltEncoder == null) {
//            System.out.println("newTypeEncoderEndian");
            TypeEncoderOperator typeEncoderOperator = new TypeEncoderOperator();
            encoder = typeEncoderOperator.newTypeEncoderEndian(rElts.get(0), endian);
        } else {
            encoder = EltEncoder;
        }

        initElts(elts, encoder); //elts -> ByteString
    }

    // InitElts initialized a.Elts, by encoding elements in to bytes.
    //
    // Since 0.2.0
    public int initElts(List<T> rElts, Encoder encoder) {
//        System.out.println("start initElts: rElts.size=" + rElts.size());
        int n = rElts.size();
        int eltsize = encoder.getEncodedSize(null);
        int sz = eltsize * n;

        StringBuffer sb = new StringBuffer();
        byte[] b = new byte[sz];
        int j = 0;
        for (int i = 0; i < n; i++) {
            byte[] bs = encoder.encode(rElts.get(i));
            for (int k = 0; k < bs.length; k++) {
                b[j++] = bs[k];
            }
        }
        array32 = array32.toBuilder().setElts(ByteString.copyFrom(b)).build();

        return n;
    }

    // Get retrieves the value at "idx" and return it.
    // If this array has a value at "idx" it returns the value and "true",
    // otherwise it returns "nil" and "false".
    //
    // Since 0.2.0
    public Pair<T, Boolean> get(int idx) {
//        System.out.println("start Base get: idx=" + idx + "   EltEncoder=" + EltEncoder);
        Pair<byte[], Boolean> tmp = getBytes(idx, EltEncoder.getEncodedSize(null));
        byte[] bs = tmp.left;
        boolean ok = tmp.right;
        if (ok) {
            return new Pair(EltEncoder.decode(bs), true);
        }

        return new Pair(null, false);
    }

    // GetBytes retrieves the raw data of value in []byte at "idx" and return it.
    //
    // Performance note
    //
    // Involves 2 memory access:
    //	 a.Bitmaps
    //	 a.Elts
    //
    // Involves 0 alloc
    //
    // Since 0.2.0
    Pair<byte[], Boolean> getBytes(int idx, int eltsize) {
//        System.out.println("start getBytes: idx=" + idx + ", eltsize=" + eltsize);
        Pair<byte[], Boolean> result = null;
        Pair<Integer, Boolean> tmp = getEltIndex(idx);
        int dataIndex = tmp.left;
        boolean ok = tmp.right;
        if (!ok) {
            return new Pair(null, false);
        }

        int stIdx = eltsize * dataIndex;
        int end = stIdx + eltsize;
        byte[] bytes = new byte[eltsize + 1];
//        System.out.println("array32.getElts():" + array32.getElts());
        array32.getElts().copyTo(bytes, stIdx, end, eltsize + 1);
        return new Pair(bytes, true);
    }

    // GetEltIndex returns the position in a.Elts of element[idx] and a bool
    // indicating if found or not.
    // If "idx" absents it returns "0, false".
    //
    // Since 0.2.0
    Pair<Integer, Boolean> getEltIndex(int idx) {
        Pair<Integer, Integer> tmp = bmBit(idx);
        int iBm = tmp.left;
        int iBit = tmp.right;

        long bmWord = array32.getBitmaps(iBm);

        if (((bmWord >> iBit) & 1) == 0) {
            return new Pair(0, false);
        }

        int base = array32.getOffsets(iBm);
        int cnt1 = BitsOperator.onesCount64(bmWord & (((long) 1 << iBit) - 1));
        return new Pair(base + cnt1, true);
    }

    // bmBit calculates bitamp word index and the bit index in the word.
    Pair<Integer, Integer> bmBit(int idx) {
        int c = idx >> bmShift;
        int r = idx & bmMask;
        return new Pair(c, r);
    }
}