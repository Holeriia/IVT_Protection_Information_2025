package main.java.rsa.core;

import main.java.rsa.model.PrivateKey;
import main.java.rsa.model.PublicKey;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * Модуль электронной подписи RSA
 */
public final class RSASignature {

    /** Создает подпись сообщения */
    public static BigInteger sign(String message, PrivateKey privateKey) {
        BigInteger m = new BigInteger(1, message.getBytes(StandardCharsets.UTF_8));
        return m.modPow(privateKey.getD(), privateKey.getN());
    }

    /** Проверяет подпись */
    public static boolean verify(String message, BigInteger signature, PublicKey publicKey) {
        BigInteger m = new BigInteger(1, message.getBytes(StandardCharsets.UTF_8));
        BigInteger verified = signature.modPow(publicKey.getE(), publicKey.getN());
        return verified.equals(m);
    }
}