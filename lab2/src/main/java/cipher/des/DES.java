package main.java.cipher.des;

import main.java.cipher.util.BitUtils;
import java.util.Arrays;

/**
 * Реализация алгоритма DES для одного блока (64-бит).
 * Используется как ядро для всех режимов работы (ECB, CFB, OFB, CBC).
 */
public class DES {

    /**
     * Шифрование (или расшифрование, т.к. DES симметричен) одного блока (64 бита).
     *
     * @param data64     блок данных (8 байт)
     * @param roundKeys  16 раундовых ключей по 48 бит
     * @return зашифрованный блок (8 байт)
     */
    public static byte[] encryptBlock(byte[] data64, int[][] roundKeys) {
        if (data64.length != 8) {
            throw new IllegalArgumentException("Блок данных должен быть 64-битным (8 байтов).");
        }

        // 1. Начальная перестановка
        int[] dataBits = BitUtils.byteArrayToBitArray(data64);
        int[] permutedData = BitUtils.performPermutation(dataBits, DesTables.IP);

        // 2. Делим на левую и правую части
        int[] L = Arrays.copyOfRange(permutedData, 0, 32);
        int[] R = Arrays.copyOfRange(permutedData, 32, 64);

        // 3. 16 раундов
        for (int i = 0; i < 16; i++) {
            int[] Lnext = R;
            int[] Rnext = BitUtils.xor(L, feistelFunction(R, roundKeys[i]));
            L = Lnext;
            R = Rnext;
        }

        // 4. Объединение R16||L16
        int[] preFinalData = new int[64];
        System.arraycopy(R, 0, preFinalData, 0, 32);
        System.arraycopy(L, 0, preFinalData, 32, 32);

        // 5. Конечная перестановка
        int[] finalBits = BitUtils.performPermutation(preFinalData, DesTables.IP_INV);

        return BitUtils.bitArrayToByteArray(finalBits);
    }

    /**
     * Функция Фейстеля f(R, K).
     */
    private static int[] feistelFunction(int[] R, int[] K) {
        int[] expandedR = BitUtils.performPermutation(R, DesTables.E);
        int[] xored = BitUtils.xor(expandedR, K);

        int[] sBoxOutput = new int[32];
        for (int i = 0; i < 8; i++) {
            int[] block6 = Arrays.copyOfRange(xored, i * 6, (i + 1) * 6);
            int row = (block6[0] << 1) | block6[5];
            int col = (block6[1] << 3) | (block6[2] << 2) | (block6[3] << 1) | block6[4];
            int sValue = DesTables.S_BOX[i][row][col];
            for (int j = 0; j < 4; j++) {
                sBoxOutput[i * 4 + j] = (sValue >> (3 - j)) & 1;
            }
        }
        return BitUtils.performPermutation(sBoxOutput, DesTables.P);
    }
}
