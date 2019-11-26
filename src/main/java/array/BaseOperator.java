package array;

import encode.TypeEncoder;
import encode.TypeEncoderOperator;
import proto.Array;

import java.util.List;

public class BaseOperator {

    public static int ArrayFlagHasEltWidth = 0x00000001;
    public static int ArrayFlagIsBitmap = 0x00000002;

    public encode.Encoder encoder;

    /**
     * @param index
     * @param elts     childv
     * @param eltWidth //index []int32, elts []uint64, eltWidth int32
     * @return
     * @throws Exception
     */
    public Bitmap16 newBitmap16(List<Integer> index, List<Long> elts, int eltWidth) throws Exception {
//        System.out.println("start newBitmap16: index.size=" + index.size() + ", elts.size=" + elts.size() + ", eltWidth=" + eltWidth);
        Bitmap16 bitmap16 = new Bitmap16();
        Array.Array32.Builder builder = bitmap16.array32.toBuilder();
        // 0{30}11
        builder.setFlags(ArrayFlagHasEltWidth | ArrayFlagIsBitmap);
        builder.setEltWidth(eltWidth);//16
        bitmap16.array32 = builder.build();

        bitmap16.initIndex(index);//generate bitmaps&offsets for 64bit-word

        builder = bitmap16.array32.toBuilder();
        BitmapOperator bitmapOperator = new BitmapOperator();
        Array.Bits bm = bitmapOperator.newBitsJoin(elts, eltWidth, false);
        builder.setBMElts(bm);//childv bitmap
        bitmap16.array32 = builder.build();

        return bitmap16;
    }

    // NewU16 creates a U16
    //
    // Since 0.2.0

    /**
     * @param index nid
     * @param elts  step
     * @return
     * @throws Exception
     */
    public U16 newU16(List<Integer> index, List<Character> elts) throws Exception {
//        System.out.println("start newU16: index.size=" + index.size() + ", elts.size=" + elts.size());
        U16 a = new U16();
        a.init(index, elts);
        return a;
    }

    // NewEmpty creates an empty Array with element of type of "v".
    // If v is a pointer, the value type it points to is used.
    //
    // Since 0.2.0
    public array.Array newEmpty(String v) throws Exception {
        TypeEncoderOperator typeEncoderOperator = new TypeEncoderOperator();
        TypeEncoder m = typeEncoderOperator.newTypeEncoder(v);
        array.Array a = new array.Array();
        a.EltEncoder = m;
        return a;
    }
}
