package main.java.cipher;

import main.java.cipher.api.BlockCipher;
import main.java.cipher.api.KuznechikCipher;
import main.java.cipher.block.CfbMode;
import main.java.cipher.util.HexUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * Главный класс демонстрации работы алгоритма Кузнечик в режиме CFB.
 * Показаны примеры шифрования и расшифрования текста
 * как с фиксированными параметрами, так и со случайно сгенерированными.
 */
public class Main {

    /**
     * Демонстрирует процесс шифрования и расшифрования строки
     * с использованием Кузнечика в режиме CFB.
     *
     * @param plaintextStr исходный открытый текст
     * @param key          ключ (32 байта)
     * @param iv           вектор инициализации (16 байт)
     */
    private static void runCfbDemo(String plaintextStr, byte[] key, byte[] iv) {
        BlockCipher kuznechik = new KuznechikCipher(key);
        CfbMode cfb = new CfbMode(kuznechik, iv);

        byte[] plaintextBytes = plaintextStr.getBytes(StandardCharsets.UTF_8);

        // Шифруем
        byte[] cipher = cfb.encrypt(plaintextBytes);

        // Дешифруем
        byte[] decrypted = cfb.decrypt(cipher);

        // Вывод результатов
        System.out.println("----------- CFB Demo (Kuznechik) -----------");
        System.out.println("Открытый текст: " + plaintextStr);
        System.out.println("Ключ:           " + HexUtils.byteArrayToHexString(key));
        System.out.println("IV:             " + HexUtils.byteArrayToHexString(iv));
        System.out.println("Шифртекст:      " + HexUtils.byteArrayToHexString(cipher));
        System.out.println("Расшифровка:    " + new String(decrypted, StandardCharsets.UTF_8));
        System.out.println(plaintextStr.equals(new String(decrypted, StandardCharsets.UTF_8)) ? "✅ УСПЕХ\n" : "❌ ОШИБКА\n");
    }

    /**
     * Генерация случайного массива байт заданной длины
     */
    private static byte[] randomBytes(int length) {
        byte[] arr = new byte[length];
        new SecureRandom().nextBytes(arr);
        return arr;
    }

    public static void main(String[] args) {
        // 1. Демонстрация с фиксированными ключом и IV
        byte[] keyFixed = HexUtils.hexStringToByteArray("8899AABBCCDDEEFF0011223344556677FEDCBA98765432100123456789ABCDEF");
        byte[] ivFixed  = HexUtils.hexStringToByteArray("00112233445566778899AABBCCDDEEFF");
        runCfbDemo("Нижегородский государственный технический университет", keyFixed, ivFixed);

        // 2. Демонстрация со случайно сгенерированными ключом и IV
        runCfbDemo("Серова Валерия Павловна", randomBytes(32), randomBytes(16));
    }
}
