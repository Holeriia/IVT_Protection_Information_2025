package main.java.cipher;

import main.java.cipher.block.CfbMode;
import main.java.cipher.des.DesCipher;
import main.java.cipher.util.HexUtils;

//ля преобразования между String и byte[]
import java.nio.charset.StandardCharsets;
//Генератор криптографически стойких случайных чисел
import java.security.SecureRandom;

public class Main {

    private static void runCfbDemo(String plaintextStr, byte[] key, byte[] iv) {
        CfbMode cfb = new CfbMode(new DesCipher(key), iv);

        byte[] cipher    = cfb.encrypt(plaintextStr.getBytes(StandardCharsets.UTF_8));
        byte[] decrypted = cfb.decrypt(cipher);

        System.out.println("----------- CFB Demo -----------");
        System.out.println("Открытый текст: " + plaintextStr);
        System.out.println("Ключ:           " + HexUtils.byteArrayToHexString(key));
        System.out.println("IV:             " + HexUtils.byteArrayToHexString(iv));
        System.out.println("Шифртекст:      " + HexUtils.byteArrayToHexString(cipher));
        System.out.println("Расшифровка:    " + new String(decrypted, StandardCharsets.UTF_8));
        System.out.println(plaintextStr.equals(new String(decrypted, StandardCharsets.UTF_8)) ? "✅УСПЕХ (CFB)\n" : "❌ОШИБКА (CFB)\n");
    }

    private static byte[] toBytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] randomBytes8() {
        byte[] arr = new byte[8];
        new SecureRandom().nextBytes(arr);
        return arr;
    }

    public static void main(String[] args) {
        // Пример с фиксированными ключом и IV
        runCfbDemo("Нижегородский государственный технический университет", toBytes("12345678"), toBytes("ABCDEFGH"));
        // Пример со случайными ключом и IV
        runCfbDemo("Серова Валерия Павловна", randomBytes8(), randomBytes8());
    }
}
