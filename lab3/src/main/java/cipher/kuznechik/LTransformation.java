package main.java.cipher.kuznechik;

import main.java.cipher.util.HexUtils;

/**
 * Реализация линейного преобразования L алгоритма Кузнечик.
 * Использует операции в поле GF(2^8) и вектор коэффициентов l_vec.
 */
public class LTransformation {

    // Вектор коэффициентов для L-преобразования
    private static final byte[] l_vec = {
            (byte) 0x94, 0x20, (byte) 0x85, 0x10,
            (byte) 0xC2, (byte) 0xC0, 0x01, (byte) 0xFB,
            0x01, (byte) 0xC0, (byte) 0xC2, 0x10,
            (byte) 0x85, 0x20, (byte) 0x94, 0x01
    };

    /** Умножение двух байт в поле GF(2^8) с редукцией по полиному x⁸ + x⁷ + x⁶ + x + 1 (0xC3). */
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

    /** Одно R-преобразование — сдвиг и вычисление нового байта с помощью вектора l_vec. */
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

    /** Полное L-преобразование — 16 последовательных R-преобразований. */
    public static byte[] L(byte[] state) {
        byte[] out = state.clone();
        for (int i = 0; i < 16; i++) out = R(out);
        return out;
    }

    /** Обратное к R-преобразование. */
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

    /** Обратное L-преобразование — 16 последовательных reverseR. */
    public static byte[] reverseL(byte[] state) {
        byte[] out = state.clone();
        for (int i = 0; i < 16; i++) out = reverseR(out);
        return out;
    }

    /** Пример проверки корректности L и reverseL. */
    public static void main(String[] args) {
        byte[] test = new byte[16];
        for (int i = 0; i < 16; i++) test[i] = (byte) i;

        byte[] lOut = L(test);
        byte[] lRev = reverseL(lOut);

        System.out.println("L:          " + HexUtils.byteArrayToHexString(lOut));
        System.out.println("reverseL:   " + HexUtils.byteArrayToHexString(lRev));
    }
}