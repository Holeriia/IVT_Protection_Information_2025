package main.java.cipher.kuznechik;

import main.java.cipher.util.HexUtils;

public class LTransformation {

    private static final byte[] l_vec = {
            (byte)148, 32, (byte)133, 16, (byte)194, (byte)192, 1, (byte)251,
            1, (byte)192, (byte)194, 16, (byte)133, 32, (byte)148, 1
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

    // ---- Обратное R ----
    public static byte[] reverseR(byte[] state) {
        byte[] out = new byte[16];
        System.arraycopy(state, 1, out, 0, 15); // сдвиг влево
        // последний байт
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
