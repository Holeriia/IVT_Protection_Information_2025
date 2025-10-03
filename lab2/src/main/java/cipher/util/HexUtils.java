package main.java.cipher.util;

/**
 * Вспомогательный класс для работы с шестнадцатеричными строками.
 * Используется для преобразования байтов в hex и обратно.
 */
public class HexUtils {

    /**
     * Преобразует шестнадцатеричную строку в массив байтов.
     *
     * @param hex строка в hex-формате (должна иметь чётную длину)
     * @return массив байтов
     * @throws IllegalArgumentException если длина строки нечётная
     */
    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Шестнадцатеричная строка должна иметь чётную длину");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Преобразует массив байтов в шестнадцатеричную строку.
     *
     * @param bytes массив байтов
     * @return строка в hex-формате (заглавные буквы)
     */
    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}