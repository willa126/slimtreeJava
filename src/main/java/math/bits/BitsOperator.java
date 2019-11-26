package math.bits;

public class BitsOperator {

    public static final long m0 = 0x5555555555555555L; // 01010101 ...
    public static final long m1 = 0x3333333333333333L; // 00110011 ...
    public static final long m2 = 0x0f0f0f0f0f0f0f0fL; // 00001111 ...
    public static final long m3 = 0x00ff00ff00ff00ffL; // etc.
    public static final long m4 = 0x0000ffff0000ffffL;

    // OnesCount64 returns the number of one bits ("population count") in x.
    public static int onesCount64(long x) { //uint64
        // Implementation: Parallel summing of adjacent bits.
        // See "Hacker's Delight", Chap. 5: Counting Bits.
        // The following pattern shows the general approach:
        //
        //   x = x>>1&(m0&m) + x&(m0&m)
        //   x = x>>2&(m1&m) + x&(m1&m)
        //   x = x>>4&(m2&m) + x&(m2&m)
        //   x = x>>8&(m3&m) + x&(m3&m)
        //   x = x>>16&(m4&m) + x&(m4&m)
        //   x = x>>32&(m5&m) + x&(m5&m)
        //   return int(x)
        //
        // Masking (& operations) can be left away when there's no
        // danger that a field's sum will carry over into the next
        // field: Since the result cannot be > 64, 8 bits is enough
        // and we can ignore the masks for the shifts by 8 and up.
        // Per "Hacker's Delight", the first line can be simplified
        // more, but it saves at best one instruction, so we leave
        // it alone for clarity.
        final long m = 1 << 64 - 1;
        x = x >> 1 & (m0 & m) + x & (m0 & m);
        x = x >> 2 & (m1 & m) + x & (m1 & m);
        x = (x >> 4 + x) & (m2 & m);
        x += x >> 8;
        x += x >> 16;
        x += x >> 32;
        return ((int) x & (1 << 7 - 1));
    }

    // LeadingZeros64 returns the number of leading zero bits in x; the result is 64 for x == 0.
    public static int leadingZeros64(long x ) {
        return 64 - Len64(x);
    }

    // Len64 returns the minimum number of bits required to represent x; the result is 0 for x == 0.
    public static int Len64(long x ){
        int n = 0;
        if(x >= 1<<32 ){
            x >>= 32;
            n = 32;
        }
        if( x >= 1<<16) {
            x >>= 16;
            n += 16;
        }
        if (x >= 1<<8) {
            x >>= 8;
            n += 8;
        }
        return n + (int)(Bits_tables.len8tab[(int)x]);
    }
}
