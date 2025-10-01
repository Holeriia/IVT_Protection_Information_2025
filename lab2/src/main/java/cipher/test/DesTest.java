package main.java.cipher.test;

import main.java.cipher.block.BlockCipher;
import main.java.cipher.des.DesCipher;
import main.java.cipher.util.HexUtils;

/**
 * Класс для проверки корректности работы DES ядра на известных векторах.
 * Используется новый интерфейс BlockCipher и класс DesCipher.
 */
public class DesTest {

    /**
     * Запускает один тест DES на известном векторе.
     */
    private static void runKnownAnswerTest(String hexPlaintext, String hexKey, String expectedCipherHex) {
        byte[] plaintext = HexUtils.hexStringToByteArray(hexPlaintext);
        byte[] key = HexUtils.hexStringToByteArray(hexKey);
        byte[] expected = HexUtils.hexStringToByteArray(expectedCipherHex);

        BlockCipher des = new DesCipher(key);
        byte[] cipher = des.encryptBlock(plaintext);
        byte[] decrypted = des.decryptBlock(cipher);

        String actualHex = HexUtils.byteArrayToHexString(cipher);
        String expectedHex = HexUtils.byteArrayToHexString(expected);
        String decryptedHex = HexUtils.byteArrayToHexString(decrypted);

        System.out.println("------------ Тест DES ------------");
        System.out.println("Открытый текст:   " + hexPlaintext);
        System.out.println("Ключ:             " + hexKey);
        System.out.println("Ожидаемый шифр:   " + expectedHex);
        System.out.println("Фактический шифр: " + actualHex);
        System.out.println("Расшифровка:      " + decryptedHex);

        if (actualHex.equals(expectedHex) && decryptedHex.equals(hexPlaintext)) {
            System.out.println("✅УСПЕХ\n");
        } else {
            System.out.println("❌ОШИБКА\n");
        }
    }

    public static void main(String[] args) {
        runKnownAnswerTest(
                "0123456789ABCDEF",
                "133457799BBCDFF1",
                "85E813540F0AB405"
        );

        runKnownAnswerTest(
                "0000000000000000",
                "0000000000000000",
                "8CA64DE9C1B123A7"
        );

        runKnownAnswerTest(
                "FFFFFFFFFFFFFFFF",
                "FFFFFFFFFFFFFFFF",
                "7359B2163E4EDC58"
        );
    }
}
