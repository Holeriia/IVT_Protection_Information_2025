package main.java.rsa.core;

import main.java.rsa.model.PrivateKey;
import main.java.rsa.model.PublicKey;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Реализация электронной цифровой подписи по RSA.
 * Алгоритм:
 *   Подпись: S = H(m)^d mod n
 *   Проверка: H(m) == S^e mod n
 */
public final class RSASignature {

    private static BigInteger hash(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(message.getBytes());
            return new BigInteger(1, bytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при хэшировании: " + e);
        }
    }

    public static BigInteger sign(String message, PrivateKey key) {
        BigInteger h = hash(message);
        return h.modPow(key.getD(), key.getN());
    }

    public static boolean verify(String message, BigInteger signature, PublicKey key) {
        BigInteger h = hash(message);
        BigInteger check = signature.modPow(key.getE(), key.getN());
        return h.equals(check);
    }
}