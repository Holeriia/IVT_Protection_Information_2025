package main.java.cipher.test;

import main.java.cipher.api.BlockCipher;
import main.java.cipher.api.KuznechikCipher;
import main.java.cipher.util.HexUtils;

/**
 * Тестовый класс для проверки реализации алгоритма Кузнечик
 */
public class KuznechikTest {

    public static void main(String[] args) {

        // Эталонные данные из ГОСТ
        String hexKey =
                "8899AABBCCDDEEFF0011223344556677" +
                        "FEDCBA98765432100123456789ABCDEF";
        String hexPlaintext = "1122334455667700FFEEDDCCBBAA9988";
        String expectedHexCipher = "7F679D90BEBC24305A468D42B9D4EDCD";

        byte[] key = HexUtils.hexStringToByteArray(hexKey);
        byte[] plaintext = HexUtils.hexStringToByteArray(hexPlaintext);

        // Создание экземпляра через интерфейс
        BlockCipher kuznechik = new KuznechikCipher(key);

        // Проверка размера блока
        if (kuznechik.getBlockSize() != 16) {
            System.out.println("⚠️ Предупреждение: размер блока не равен 16 байтам!");
        }

        // Шифрование и расшифрование
        byte[] encrypted = kuznechik.encryptBlock(plaintext);
        byte[] decrypted = kuznechik.decryptBlock(encrypted);

        // Преобразование в HEX
        String actualHexCipher = HexUtils.byteArrayToHexString(encrypted);
        String decryptedHex = HexUtils.byteArrayToHexString(decrypted);

        // Красивый вывод
        System.out.println("======= Тест алгоритма КУЗНЕЧИК =======");
        System.out.println("Открытый текст:   " + hexPlaintext);
        System.out.println("Ключ:             " + hexKey);
        System.out.println("Ожидаемый шифр:   " + expectedHexCipher);
        System.out.println("Фактический шифр: " + actualHexCipher);
        System.out.println("Расшифровка:      " + decryptedHex);

        // Проверка результата
        boolean cipherMatch = actualHexCipher.equalsIgnoreCase(expectedHexCipher);
        boolean decryptMatch = decryptedHex.equalsIgnoreCase(hexPlaintext);

        System.out.println("\nРезультат теста:");
        if (cipherMatch && decryptMatch) {
            System.out.println("✅УСПЕХ — реализация Кузнечика корректна!\n");
        } else {
            System.out.println("❌ОШИБКА — результаты не совпадают!");
            if (!cipherMatch) System.out.println("Шифр не совпадает с эталонным значением");
            if (!decryptMatch) System.out.println("Расшифровка не совпадает с исходным текстом");
            System.out.println();
        }
    }
}
