package main.java.cipher.kuznechik;

import main.java.cipher.util.XorUtils;

public class KuznechikCipher {

    private static final int BLOCK_SIZE = 16;
    private byte[][] iter_key = new byte[10][BLOCK_SIZE];

    public KuznechikCipher(byte[] key1, byte[] key2) {
        expandKey(key1, key2);
    }

    // ---- Функции F и расчёт раундовых ключей ----
    private void getC(byte[][] iterC) {
        for (int i = 0; i < 32; i++) {
            byte[] c = new byte[BLOCK_SIZE];
            c[0] = (byte) (i + 1);
            iterC[i] = LTransformation.L(c);
        }
    }

    private byte[][] F(byte[] k1, byte[] k2, byte[] iterConst) {
        byte[] temp = XorUtils.xor(k1, iterConst);
        temp = STransformation.s(temp);
        temp = LTransformation.L(temp);
        byte[] outK1 = XorUtils.xor(temp, k2);
        return new byte[][]{outK1, k1};
    }

    private void expandKey(byte[] key1, byte[] key2) {
        byte[][] iterC = new byte[32][BLOCK_SIZE];
        getC(iterC);
        iter_key[0] = key1;
        iter_key[1] = key2;

        byte[][] iter12 = new byte[][]{key1, key2};
        byte[][] iter34;

        for (int i = 0; i < 4; i++) {
            iter34 = F(iter12[0], iter12[1], iterC[0 + 8 * i]);
            iter12 = F(iter34[0], iter34[1], iterC[1 + 8 * i]);
            iter34 = F(iter12[0], iter12[1], iterC[2 + 8 * i]);
            iter12 = F(iter34[0], iter34[1], iterC[3 + 8 * i]);
            iter34 = F(iter12[0], iter12[1], iterC[4 + 8 * i]);
            iter12 = F(iter34[0], iter34[1], iterC[5 + 8 * i]);
            iter34 = F(iter12[0], iter12[1], iterC[6 + 8 * i]);
            iter12 = F(iter34[0], iter34[1], iterC[7 + 8 * i]);
            iter_key[2 * i + 2] = iter12[0];
            iter_key[2 * i + 3] = iter12[1];
        }
    }

    // ---- Шифрование блока ----
    public byte[] encryptBlock(byte[] blk) {
        byte[] out = blk.clone();
        for (int i = 0; i < 9; i++) {
            out = XorUtils.xor(iter_key[i], out);
            out = STransformation.s(out);
            out = LTransformation.L(out);
        }
        out = XorUtils.xor(iter_key[9], out);
        return out;
    }

    // ---- Дешифрование блока ----
    public byte[] decryptBlock(byte[] blk) {
        byte[] out = XorUtils.xor(iter_key[9], blk.clone());
        for (int i = 8; i >= 0; i--) {
            out = LTransformation.reverseL(out);
            out = STransformation.reverseS(out);
            out = XorUtils.xor(iter_key[i], out);
        }
        return out;
    }
}