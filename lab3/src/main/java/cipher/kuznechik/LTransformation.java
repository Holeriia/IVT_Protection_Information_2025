package main.java.cipher.kuznechik;

public class LTransformation {
    private static final byte[] l_vec = {
            1, (byte)148, 32, (byte)133, 16, (byte)194, (byte)192, 1,
            (byte)251, 1, (byte)192, (byte)194, 16, (byte)133, 32, (byte)148
    };

    private static byte gfMul(byte a, byte b) {
        byte c = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) c ^= a;
            boolean hi_bit = (a & 0x80) != 0;
            a <<= 1;
            if (hi_bit) a ^= 0xC3; // полином x^8+x^7+x^6+x+1
            b >>= 1;
        }
        return c;
    }

    public static byte[] R(byte[] state) {
        byte a_15 = 0;
        byte[] internal = new byte[state.length];
        for (int i = 15; i >= 0; i--) {
            internal[i == 0 ? 15 : i - 1] = state[i];
            a_15 ^= gfMul(state[i], l_vec[i]);
        }
        internal[15] = a_15;
        return internal;
    }

    public static byte[] L(byte[] in) {
        byte[] internal = in.clone();
        for (int i = 0; i < 16; i++)
            internal = R(internal);
        return internal;
    }

    public static byte[] reverseR(byte[] state) {
        byte a_0 = state[15];
        byte[] internal = new byte[state.length];
        for (int i = 1; i < 16; i++) {
            internal[i] = state[i - 1];
            a_0 ^= gfMul(internal[i], l_vec[i]);
        }
        internal[0] = a_0;
        return internal;
    }

    public static byte[] reverseL(byte[] in) {
        byte[] internal = in.clone();
        for (int i = 0; i < 16; i++)
            internal = reverseR(internal);
        return internal;
    }
}