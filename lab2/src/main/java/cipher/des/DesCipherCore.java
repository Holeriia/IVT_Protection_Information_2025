package main.java.cipher.des;

import main.java.cipher.Main;
import main.java.cipher.util.BitUtils;

import java.util.Arrays;

/**
 * Класс, реализующий основное блочное шифрование DES (64-битный блок).
 */
public class DesCipherCore {

    /**
     * Основная функция DES: шифрует один 64-битный блок данных.
     * Поскольку DES симметричен, эта функция используется как для Encrypt, так и для Decrypt
     * в режимах, использующих только DES_Encrypt (например, CFB, OFB).
     *
     * @param data64 64-битный блок данных (8 байтов).
     * @param roundKeys16 16 48-битных раундовых ключей.
     * @return 64-битный блок шифротекста (8 байтов).
     */
    public static byte[] encryptBlock(byte[] data64, int[][] roundKeys16) {
        if (data64.length != 8) {
            throw new IllegalArgumentException("Блок данных должен быть 64-битным (8 байтов).");
        }

        // 1. Начальная перестановка (IP)
        int[] dataBits = BitUtils.byteArrayToBitArray(data64);
        int[] permutedData = BitUtils.performPermutation(dataBits, DesTables.IP);

        // --- ВРЕМЕННЫЙ КОД ДЛЯ ОТЛАДКИ ---
        byte[] permutedBytes = BitUtils.bitArrayToByteArray(permutedData);
        System.out.println("DEBUG: Данные после IP (Hex): " + Main.byteArrayToHexString(permutedBytes));
// --- КОНЕЦ ВРЕМЕННОГО КОДА ---

        // 2. Разделение на L_0 и R_0 (32 бита)
        int[] L = Arrays.copyOfRange(permutedData, 0, 32);
        int[] R = Arrays.copyOfRange(permutedData, 32, 64);

        // 3. 16 Раундов Фейстеля
        for (int i = 0; i < 16; i++) {
            // L_i = R_i-1
            int[] L_next = R;

            // R_i = L_i-1 XOR f(R_i-1, K_i)
            int[] R_next = BitUtils.xor(L, feistelFunction(R, roundKeys16[i]));

            // Обновляем L и R для следующего раунда
            L = L_next;
            R = R_next;
        }

        // 4. Объединение R_16 и L_16
        int[] preFinalData = new int[64];
        System.arraycopy(R, 0, preFinalData, 0, 32);
        System.arraycopy(L, 0, preFinalData, 32, 32);

        // 5. Конечная перестановка (FP = IP_INV)
        int[] finalCipherBits = BitUtils.performPermutation(preFinalData, DesTables.IP_INV);

        // 6. Преобразование в байты и возврат
        return BitUtils.bitArrayToByteArray(finalCipherBits);
    }

    /**
     * Функция Фейстеля f(R, K).
     *
     * @param R 32-битная правая половина.
     * @param K 48-битный раундовый ключ.
     * @return 32-битный результат функции f.
     */
    private static int[] feistelFunction(int[] R, int[] K) {
        // 1. Расширение (E-Box: 32 -> 48 бит)
        int[] expandedR = BitUtils.performPermutation(R, DesTables.E);

        // 2. XOR с раундовым ключом (48 бит)
        int[] xoredR = BitUtils.xor(expandedR, K);

        // 3. S-Box (Подстановка: 48 -> 32 бит)
        int[] sBoxOutput = new int[32];
        for (int i = 0; i < 8; i++) {
            // Разбиваем xoredR на 8 блоков по 6 бит (для S_i)
            int[] block6 = Arrays.copyOfRange(xoredR, i * 6, (i + 1) * 6);

            // Вычисление строки (row): биты 1 и 6 (индексы 0 и 5)
            int row = (block6[0] << 1) | block6[5];

            // Вычисление столбца (col): биты 2, 3, 4, 5 (индексы 1, 2, 3, 4)
            int col = (block6[1] << 3) | (block6[2] << 2) | (block6[3] << 1) | block6[4];

            // Получаем 4-битное значение из S-Box
            int sBoxValue = DesTables.S_BOX[i][row][col];

            // Преобразуем 4-битное число в 4 отдельных бита и записываем в sBoxOutput
            for (int j = 0; j < 4; j++) {
                sBoxOutput[i * 4 + j] = (sBoxValue >> (3 - j)) & 1;
            }
        }

        // 4. Перестановка P (32 -> 32 бит)
        return BitUtils.performPermutation(sBoxOutput, DesTables.P);
    }
}