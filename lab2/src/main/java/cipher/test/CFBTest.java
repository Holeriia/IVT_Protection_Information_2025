package main.java.cipher.test;

import main.java.cipher.block.CfbMode;
import main.java.cipher.des.DesCipher;
import main.java.cipher.util.HexUtils;

//готовые библиотеки для проверки
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Класс для проверки корректности работы режима шифрования CFB
 */
public class CFBTest {

    private static void runCfbDes(String pt, String key, String iv) {
        byte[] keyBytes = HexUtils.hexStringToByteArray(key);
        byte[] ivBytes  = HexUtils.hexStringToByteArray(iv);
        byte[] ptBytes  = HexUtils.hexStringToByteArray(pt);

        // --- Наш алгоритм ---
        CfbMode cfb = new CfbMode(new DesCipher(keyBytes), ivBytes);
        byte[] cipher    = cfb.encrypt(ptBytes);
        byte[] decrypted = cfb.decrypt(cipher);

        String cipherHex    = HexUtils.byteArrayToHexString(cipher);
        String decryptedHex = HexUtils.byteArrayToHexString(decrypted);

        // --- Эталонная реализация (JCE) ---
        String jceCipherHex = runJavaCrypto(ptBytes, keyBytes, ivBytes);

        // --- Вывод ---
        System.out.printf("""
                ------------- CFB-DES -------------
                Открытый текст: %s
                Ключ:           %s
                IV:             %s
                Наш шифртекст:  %s
                JCE шифртекст:  %s
                Расшифровка:    %s
                Совпадение:     %s
                %s

                """,
                pt, key, iv,
                cipherHex, jceCipherHex,
                decryptedHex,
                cipherHex.equals(jceCipherHex) ? "✅ Шифрование совпадает" : "❌ НЕСОВПАДЕНИЕ",
                decryptedHex.equals(pt) ? "✅ Дешифрование успешно" : "❌ Ошибка при дешифровании"
        );
    }

    /**
     * Запуск эталонного DES/CFB/NoPadding через стандартную библиотеку Java.
     */
    private static String runJavaCrypto(byte[] plaintext, byte[] key, byte[] iv) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, "DES");
            Cipher cipher = Cipher.getInstance("DES/CFB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] result = cipher.doFinal(plaintext);
            return HexUtils.byteArrayToHexString(result);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении JCE шифрования", e);
        }
    }

    public static void main(String[] args) {
        runCfbDes("0123456789ABCDEF", "133457799BBCDFF1", "1234567890ABCDEF");
        runCfbDes("0011223344556677", "AABB09182736CCDD", "0F0E0D0C0B0A0908");
    }
}
