package main.java.rsa.core;

import main.java.rsa.model.PrivateKey;
import main.java.rsa.model.PublicKey;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Модуль шифрования и дешифрования RSA
 */
public final class RSAEncryptor {

    /** Шифрует текст с помощью открытого ключа */
    public static String encrypt(String message, PublicKey publicKey) {
        BigInteger m = new BigInteger(1, message.getBytes(StandardCharsets.UTF_8));
        BigInteger c = m.modPow(publicKey.getE(), publicKey.getN());
        return Base64.getEncoder().encodeToString(c.toByteArray());
    }

    /** Расшифровывает Base64-строку с помощью закрытого ключа */
    public static String decrypt(String base64Cipher, PrivateKey privateKey) {
        byte[] cipherBytes = Base64.getDecoder().decode(base64Cipher);
        BigInteger c = new BigInteger(1, cipherBytes);
        BigInteger decrypted = c.modPow(privateKey.getD(), privateKey.getN());
        byte[] decryptedBytes = decrypted.toByteArray();

        // удаляем возможный ведущий ноль
        if (decryptedBytes.length > 1 && decryptedBytes[0] == 0) {
            byte[] tmp = new byte[decryptedBytes.length - 1];
            System.arraycopy(decryptedBytes, 1, tmp, 0, tmp.length);
            decryptedBytes = tmp;
        }

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}