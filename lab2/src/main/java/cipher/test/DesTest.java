package main.java.cipher.test;

import main.java.cipher.des.DES;
import main.java.cipher.des.KeyScheduler;
import main.java.cipher.util.HexUtils;

/**
 * Класс для проверки корректности работы DES ядра на известных векторах
 */
public class DesTest {

    //Запускает один тест DES
    private static void runKnownAnswerTest(String hexPlaintext, String hexKey, String expectedCipherHex) {
        byte[] plaintext = HexUtils.hexStringToByteArray(hexPlaintext);
        byte[] key = HexUtils.hexStringToByteArray(hexKey);
        byte[] expected = HexUtils.hexStringToByteArray(expectedCipherHex);

        int[][] roundKeys = KeyScheduler.generateKeys(key);
        byte[] cipher = DES.encryptBlock(plaintext, roundKeys);

        String actualHex = HexUtils.byteArrayToHexString(cipher);
        String expectedHex = HexUtils.byteArrayToHexString(expected);

        System.out.println("--- DES Test ---");
        System.out.println("Plaintext: " + hexPlaintext);
        System.out.println("Key:       " + hexKey);
        System.out.println("Expected:  " + expectedHex);
        System.out.println("Actual:    " + actualHex);

        if (actualHex.equals(expectedHex)) {
            System.out.println("✅ SUCCESS\n");
        } else {
            System.out.println("❌ FAILURE\n");
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