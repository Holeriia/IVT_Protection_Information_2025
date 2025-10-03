package main.java.cipher;

import main.java.cipher.block.CfbMode;
import main.java.cipher.des.DesCipher;
import main.java.cipher.util.HexUtils;

// Для преобразования строк в массивы байт
import java.nio.charset.StandardCharsets;
// Генератор криптографически стойких случайных чисел
import java.security.SecureRandom;

/**
 * Главный класс демонстрации работы алгоритма DES в режиме CFB.
 * Показаны примеры шифрования и расшифрования текста
 * как с фиксированными параметрами, так и со случайно сгенерированными.
 */
public class Main {

    /**
     * Демонстрирует процесс шифрования и расшифрования строки
     * с использованием DES в режиме CFB.
     *
     * @param plaintextStr исходный открытый текст
     * @param key          ключ (8 байт)
     * @param iv           вектор инициализации (8 байт)
     */
    private static void runCfbDemo(String plaintextStr, byte[] key, byte[] iv) {
        CfbMode cfb = new CfbMode(new DesCipher(key), iv);

        // Шифруем и расшифровываем текст
        byte[] cipher    = cfb.encrypt(plaintextStr.getBytes(StandardCharsets.UTF_8));
        byte[] decrypted = cfb.decrypt(cipher);

        // Вывод результатов
        System.out.println("----------- CFB Demo -----------");
        System.out.println("Открытый текст: " + plaintextStr);
        System.out.println("Ключ:           " + HexUtils.byteArrayToHexString(key));
        System.out.println("IV:             " + HexUtils.byteArrayToHexString(iv));
        System.out.println("Шифртекст:      " + HexUtils.byteArrayToHexString(cipher));
        System.out.println("Расшифровка:    " + new String(decrypted, StandardCharsets.UTF_8));
        System.out.println(plaintextStr.equals(new String(decrypted, StandardCharsets.UTF_8)) ? "✅ УСПЕХ\n" : "❌ ОШИБКА\n");
    }

    /**
     * Утилита для преобразования строки в массив байт (UTF-8).
     */
    private static byte[] toBytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Генерация случайного массива длиной 8 байт
     * (для ключа или IV в DES).
     */
    private static byte[] randomBytes8() {
        byte[] arr = new byte[8];
        new SecureRandom().nextBytes(arr);
        return arr;
    }

    /**
     * Точка входа в программу.
     * Запускаются две демонстрации:
     *  1. С фиксированными ключом и IV.
     *  2. Со случайно сгенерированными ключом и IV.
     */
    public static void main(String[] args) {
        runCfbDemo("Нижегородский государственный технический университет", toBytes("12345678"), toBytes("ABCDEFGH"));
        runCfbDemo("Серова Валерия Павловна", randomBytes8(), randomBytes8());
    }
}