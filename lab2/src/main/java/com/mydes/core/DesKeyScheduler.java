package main.java.com.mydes.core;

import java.util.Arrays;

/**
 * Класс, отвечающий за генерацию 16 раундовых ключей DES (Key Schedule).
 */
public class DesKeyScheduler {

    /**
     * Генерирует 16 48-битных раундовых ключей из 64-битного мастер-ключа.
     *
     * @param masterKey64 64-битный ключ (8 байтов).
     * @return Двумерный массив int[16][48], представляющий 16 раундовых ключей (массивы битов).
     */
    public static int[][] generateKeys(byte[] masterKey64) {
        if (masterKey64.length != 8) {
            throw new IllegalArgumentException("Мастер-ключ должен быть 64-битным (8 байтов).");
        }

        // 1. Преобразование в массив битов
        int[] keyBits = BitUtils.byteArrayToBitArray(masterKey64);

        // 2. Применение PC-1 (Перестановка/Сжатие 64 -> 56 бит)
        int[] key56 = BitUtils.performPermutation(keyBits, DesTables.PC_1);

        // 3. Разделение на две 28-битные части (C и D)
        int[] C = Arrays.copyOfRange(key56, 0, 28);
        int[] D = Arrays.copyOfRange(key56, 28, 56);

        int[][] roundKeys = new int[16][48];

        // 4. Цикл 16 раундов для генерации ключей
        for (int i = 0; i < 16; i++) {
            int shifts = DesTables.SHIFTS[i];

            // 4a. Циклический сдвиг влево
            C = BitUtils.circularShiftLeft(C, shifts);
            D = BitUtils.circularShiftLeft(D, shifts);

            // 4b. Объединение C и D (56 бит)
            int[] C_D_56 = new int[56];
            System.arraycopy(C, 0, C_D_56, 0, 28);
            System.arraycopy(D, 0, C_D_56, 28, 28);

            // 4c. Применение PC-2 (Перестановка/Сжатие 56 -> 48 бит)
            roundKeys[i] = BitUtils.performPermutation(C_D_56, DesTables.PC_2);
        }

        return roundKeys;
    }
}