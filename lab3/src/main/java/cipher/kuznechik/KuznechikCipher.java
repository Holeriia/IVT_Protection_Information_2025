package main.java.cipher.kuznechik;

import main.java.cipher.util.HexUtils;
import main.java.cipher.util.XorUtils;

public class KuznechikCipher {

    private static final int BLOCK_SIZE = 16;
    private byte[][] iter_key = new byte[10][BLOCK_SIZE];

    public KuznechikCipher(byte[] key1, byte[] key2) {
        expandKey(key1, key2);
    }

    private void getC(byte[][] iterC) {
        for (int i = 0; i < 32; i++) {
            byte[] c = new byte[BLOCK_SIZE];
            c[15] = (byte) (i + 1);  // только последний байт
            iterC[i] = LTransformation.L(c);
        }
    }

    private byte[][] F(byte[] k1, byte[] k2, byte[] iterConst) {
        byte[] t = XorUtils.xor(k1, iterConst); // 1. XOR с константой
        t = STransformation.s(t);               // 2. S-преобразование
        t = LTransformation.L(t);               // 3. L-преобразование
        byte[] newK1 = XorUtils.xor(k2, t);     // 4. XOR с k2
        return new byte[][]{newK1, k1.clone()}; // меняем местами
    }

    private void expandKey(byte[] key1, byte[] key2) {
        byte[][] iterC = new byte[32][BLOCK_SIZE];
        getC(iterC);

        byte[] k1 = key1.clone();
        byte[] k2 = key2.clone();

        iter_key[0] = k1.clone();
        iter_key[1] = k2.clone();

        int keyIndex = 2;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                byte[][] t = F(k1, k2, iterC[i * 8 + j]);
                k1 = t[0];
                k2 = t[1];
            }
            iter_key[keyIndex++] = k1.clone();
            iter_key[keyIndex++] = k2.clone();
        }
    }

    public byte[] encryptBlock(byte[] blk) {
        if (blk.length != BLOCK_SIZE) throw new IllegalArgumentException("Block must be 16 bytes");
        byte[] out = blk.clone();
        for (int i = 0; i < 9; i++) {
            out = XorUtils.xor(iter_key[i], out);
            out = STransformation.s(out);
            out = LTransformation.L(out);
        }
        out = XorUtils.xor(iter_key[9], out); // финальный XOR
        return out;
    }

    public byte[] decryptBlock(byte[] blk) {
        if (blk.length != BLOCK_SIZE) throw new IllegalArgumentException("Block must be 16 bytes");
        byte[] out = XorUtils.xor(iter_key[9], blk.clone());
        for (int i = 8; i >= 0; i--) {
            out = LTransformation.reverseL(out);
            out = STransformation.reverseS(out);
            out = XorUtils.xor(iter_key[i], out);
        }
        return out;
    }

    public static void main(String[] args) {
        byte[] key1 = HexUtils.hexStringToByteArray("8899AABBCCDDEEFF0011223344556677");
        byte[] key2 = HexUtils.hexStringToByteArray("FEDCBA98765432100123456789ABCDEF");

        KuznechikCipher cipher = new KuznechikCipher(key1, key2);

        byte[] block = HexUtils.hexStringToByteArray("1122334455667700FFEEDDCCBBAA9988");
        byte[] encrypted = cipher.encryptBlock(block);
        byte[] decrypted = cipher.decryptBlock(encrypted);

        System.out.println("Зашифрованный блок: " + HexUtils.byteArrayToHexString(encrypted));
        System.out.println("Расшифрованный блок: " + HexUtils.byteArrayToHexString(decrypted));
    }
}
