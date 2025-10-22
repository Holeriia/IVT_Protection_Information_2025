package main.java.rsa.core;

import main.java.rsa.model.PublicKey;
import main.java.rsa.model.PrivateKey;
import java.math.BigInteger;

/**
 * Класс для шифрования и расшифровки данных по алгоритму RSA.
 */
public final class RSAEncryptor {

    /**
     * Шифрование числа (например, текст в виде BigInteger)
     */
    public static BigInteger encrypt(BigInteger message, PublicKey key) {
        return message.modPow(key.getE(), key.getN());
    }

    /**
     * Расшифровка числа
     */
    public static BigInteger decrypt(BigInteger cipher, PrivateKey key) {
        return cipher.modPow(key.getD(), key.getN());
    }

    /**
     * Преобразование строки в BigInteger и обратно
     */
    public static BigInteger textToNumber(String text) {
        return new BigInteger(text.getBytes());
    }

    public static String numberToText(BigInteger number) {
        return new String(number.toByteArray());
    }
}
