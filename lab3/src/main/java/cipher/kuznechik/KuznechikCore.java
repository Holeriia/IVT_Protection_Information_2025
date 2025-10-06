package main.java.cipher.kuznechik;

import main.java.cipher.util.HexUtils;
import main.java.cipher.util.XorUtils;

/**
 * Основное ядро алгоритма Кузнечик (GOST R 34.12–2015).
 * Реализует генерацию ключей и операции шифрования/дешифрования блоков.
 */
public class KuznechikCore {

    private static final int BLOCK_SIZE = 16;
    private byte[][] iter_key = new byte[10][BLOCK_SIZE];

    /** Инициализация с двумя 16-байтовыми половинами ключа. */
    public KuznechikCore(byte[] key1, byte[] key2) {
        expandKey(key1, key2);
    }

    /** Генерация 32 констант C для ключевого расписания. */
    private void getC(byte[][] iterC) {
        for (int i = 0; i < 32; i++) {
            byte[] c = new byte[BLOCK_SIZE];
            c[15] = (byte) (i + 1); // только младший байт содержит номер
            iterC[i] = LTransformation.L(c);
        }
    }

    /** Одна итерация функции F — часть ключевого расписания. */
    private byte[][] F(byte[] k1, byte[] k2, byte[] iterConst) {
        byte[] t = XorUtils.xor(k1, iterConst); // 1. XOR с константой
        t = STransformation.s(t);               // 2. S-преобразование
        t = LTransformation.L(t);               // 3. L-преобразование
        byte[] newK1 = XorUtils.xor(k2, t);     // 4. XOR с k2
        return new byte[][]{newK1, k1.clone()}; // меняем местами
    }

    /** Расширение исходного ключа на 10 раундовых ключей. */
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

    /** Шифрует один 16-байтовый блок. */
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

    /** Расшифровывает один 16-байтовый блок. */
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

    /** Тест работы шифра с примером из стандарта. */
    public static void main(String[] args) {
        byte[] key1 = HexUtils.hexStringToByteArray("8899AABBCCDDEEFF0011223344556677");
        byte[] key2 = HexUtils.hexStringToByteArray("FEDCBA98765432100123456789ABCDEF");

        KuznechikCore cipher = new KuznechikCore(key1, key2);

        byte[] block = HexUtils.hexStringToByteArray("1122334455667700FFEEDDCCBBAA9988");
        byte[] encrypted = cipher.encryptBlock(block);
        byte[] decrypted = cipher.decryptBlock(encrypted);

        System.out.println("Зашифрованный блок: " + HexUtils.byteArrayToHexString(encrypted));
        System.out.println("Расшифрованный блок: " + HexUtils.byteArrayToHexString(decrypted));
    }
}
