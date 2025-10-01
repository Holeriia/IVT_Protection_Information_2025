package main.java.com.mydes;

import main.java.com.mydes.core.DesCipherCore;
import main.java.com.mydes.core.DesKeyScheduler;

/**
 * Главный класс для тестирования и демонстрации работы алгоритма DES Core.
 */
public class Main {

    /**
     * Преобразует шестнадцатеричную строку в массив байтов.
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Преобразует массив байтов в шестнадцатеричную строку.
     */
    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // --- Тестовый вектор DES (известный) ---
        String PLAIN_HEX = "0123456789ABCDEF";
        String KEY_HEX = "133457799BBCDFEF";
        String EXPECTED_CIPHER_HEX = "85E813540F0AB405";

        System.out.println("--- Проверка ядра DES (блочное шифрование) ---");
        System.out.println("Исходный текст (Hex): " + PLAIN_HEX);
        System.out.println("Ключ (Hex):          " + KEY_HEX);
        System.out.println("Ожидаемый Cipher (Hex): " + EXPECTED_CIPHER_HEX);
        System.out.println("-------------------------------------------------");

        try {
            // 1. Подготовка данных
            byte[] plaintext = hexStringToByteArray(PLAIN_HEX);
            byte[] masterKey = hexStringToByteArray(KEY_HEX);

            // 2. Генерация раундовых ключей
            int[][] roundKeys = DesKeyScheduler.generateKeys(masterKey);
            System.out.println("Сгенерировано " + roundKeys.length + " раундовых ключей.");
            // Для быстрой проверки: можно вывести первый ключ
            // System.out.println("Первый ключ (48 бит): " + Arrays.toString(roundKeys[0]));

            // 3. Шифрование блока
            byte[] actualCiphertext = DesCipherCore.encryptBlock(plaintext, roundKeys);
            String actualCipherHex = byteArrayToHexString(actualCiphertext);

            System.out.println("Полученный Cipher (Hex): " + actualCipherHex);

            // 4. Сравнение результата
            if (actualCipherHex.equalsIgnoreCase(EXPECTED_CIPHER_HEX)) {
                System.out.println("\n✅ УСПЕХ: Ядро DES работает корректно по тестовому вектору!");
            } else {
                System.out.println("\n❌ ОШИБКА: Полученный шифротекст не совпадает с ожидаемым.");
                System.out.println("    Разница: " + actualCipherHex + " (Actual) != " + EXPECTED_CIPHER_HEX + " (Expected)");
            }

        } catch (Exception e) {
            System.err.println("Произошла ошибка при тестировании DES Core:");
            e.printStackTrace();
        }
    }
}