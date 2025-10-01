package main.java.com.mydes.core;

/**
        * Вспомогательный класс для низкоуровневых операций с битами.
 */
public class BitUtils {

    // Запрещаем создание экземпляров
    private BitUtils() {}

    /**
     * Преобразует массив байтов (8 бит) в массив целых чисел (каждое целое = 1 бит).
     *
     * @param bytes Входной массив байтов (например, 8 байт = 64 бита).
     * @return Массив битов (0 или 1).
     */
    public static int[] byteArrayToBitArray(byte[] bytes) {
        int[] bits = new int[bytes.length * 8];
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < 8; j++) {
                // Извлекаем j-й бит из i-го байта.
                // & 1 для получения 0 или 1.
                // DES использует Big Endian (бит 7 - самый старший, бит 0 - самый младший).
                // Бит 7 находится в позиции 7, бит 0 в позиции 0.
                bits[i * 8 + j] = (bytes[i] >>> (7 - j)) & 1;
            }
        }
        return bits;
    }

    /**
     * Преобразует массив битов (0 или 1) в массив байтов.
     *
     * @param bits Входной массив битов. Длина должна быть кратна 8.
     * @return Массив байтов.
     */
    public static byte[] bitArrayToByteArray(int[] bits) {
        if (bits.length % 8 != 0) {
            throw new IllegalArgumentException("Длина массива битов должна быть кратна 8.");
        }
        byte[] bytes = new byte[bits.length / 8];
        for (int i = 0; i < bytes.length; i++) {
            int currentByte = 0;
            for (int j = 0; j < 8; j++) {
                // Сдвигаем текущий байт влево и добавляем следующий бит.
                currentByte = (currentByte << 1) | bits[i * 8 + j];
            }
            bytes[i] = (byte) currentByte;
        }
        return bytes;
    }

    /**
     * Выполняет перестановку входного массива битов согласно таблице.
     *
     * @param input Массив битов (0 или 1).
     * @param permutationTable Таблица перестановки (0-базовые индексы).
     * @return Новый массив битов после перестановки.
     */
    public static int[] performPermutation(int[] input, int[] permutationTable) {
        int[] output = new int[permutationTable.length];
        for (int i = 0; i < permutationTable.length; i++) {
            // Индекс i-го бита в output берется из элемента permutationTable[i]
            // во входном массиве input.
            int sourceIndex = permutationTable[i];

            // Защита от выхода за пределы, хотя не должно случиться, если таблицы верны.
            if (sourceIndex >= input.length || sourceIndex < 0) {
                throw new IllegalArgumentException("Некорректный индекс в таблице перестановки: " + sourceIndex);
            }

            output[i] = input[sourceIndex];
        }
        return output;
    }

    /**
     * Выполняет циклический сдвиг влево на заданное количество позиций.
     *
     * @param input Массив битов.
     * @param shifts Количество позиций для циклического сдвига.
     * @return Новый массив битов после сдвига.
     */
    public static int[] circularShiftLeft(int[] input, int shifts) {
        int length = input.length;
        int[] output = new int[length];

        // Нормализация сдвигов
        shifts = shifts % length;
        if (shifts < 0) {
            shifts += length; // Обработка отрицательных сдвигов, хотя здесь не нужно.
        }

        for (int i = 0; i < length; i++) {
            // Новый индекс = (текущий индекс + длина - сдвиг) % длина
            // Более просто: бит, который был на позиции (i + shifts) % length,
            // теперь находится на позиции i.
            output[i] = input[(i + shifts) % length];
        }
        return output;
    }

    /**
     * Выполняет побитовую операцию XOR для двух массивов битов.
     *
     * @param a Первый массив.
     * @param b Второй массив.
     * @return Результат XOR.
     */
    public static int[] xor(int[] a, int[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Длины массивов для XOR должны совпадать.");
        }
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] ^ b[i]; // Используем оператор XOR
        }
        return result;
    }
}
