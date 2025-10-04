package main.java.cipher.kuznechik;

import main.java.cipher.util.HexUtils;

public class LTransformation {

    // исправленный вектор L
    private static final byte[] l_vec = {
            (byte)0x94, 0x20, (byte)0x85, 0x10,
            (byte)0xC2, (byte)0xC0, 0x01, (byte)0xFB,
            0x01, (byte)0xC0, (byte)0xC2, 0x10,
            (byte)0x85, 0x20, (byte)0x94, 0x01
    };

    private static byte gfMul(byte a, byte b) {
        int aa = a & 0xFF;
        int bb = b & 0xFF;
        int res = 0;
        for (int i = 0; i < 8; i++) {
            if ((bb & 1) != 0) res ^= aa;
            boolean hi = (aa & 0x80) != 0;
            aa = (aa << 1) & 0xFF;
            if (hi) aa ^= 0xC3;
            bb >>= 1;
        }
        return (byte) (res & 0xFF);
    }

    public static byte[] R(byte[] state) {
        byte a = 0;
        for (int i = 0; i < 16; i++) {
            a ^= gfMul(state[i], l_vec[i]);
        }
        byte[] out = new byte[16];
        System.arraycopy(state, 0, out, 1, 15);
        out[0] = a;
        return out;
    }

    public static byte[] L(byte[] state) {
        byte[] out = state.clone();
        for (int i = 0; i < 16; i++) out = R(out);
        return out;
    }

    public static byte[] reverseR(byte[] state) {
        byte[] out = new byte[16];
        System.arraycopy(state, 1, out, 0, 15);
        byte last = state[0];
        for (int i = 0; i < 15; i++) {
            last ^= gfMul(out[i], l_vec[i]);
        }
        out[15] = last;
        return out;
    }

    public static byte[] reverseL(byte[] state) {
        byte[] out = state.clone();
        for (int i = 0; i < 16; i++) out = reverseR(out);
        return out;
    }

    public static void main(String[] args) {
        byte[] test = new byte[16];
        for (int i = 0; i < 16; i++) test[i] = (byte)i;

        byte[] lOut = L(test);
        byte[] lRev = reverseL(lOut);

        System.out.println("L:          " + HexUtils.byteArrayToHexString(lOut));
        System.out.println("reverseL:   " + HexUtils.byteArrayToHexString(lRev));
    }
}
