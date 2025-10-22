package main.java.rsa.core;

import main.java.rsa.model.PrivateKey;
import main.java.rsa.model.PublicKey;
import main.java.rsa.model.RSAKeyPair;

import java.math.BigInteger;

/**
 * Демонстрация работы RSA: генерация, шифрование, подпись.
 */
public final class RSADemo {

    public static void run(String message) {
        System.out.println("=== RSA Лабораторная ===");

        KeyGenerator generator = new KeyGenerator();
        RSAKeyPair keyPair = generator.generate(1024);

        PublicKey pub = keyPair.getPublicKey();
        PrivateKey priv = keyPair.getPrivateKey();

        System.out.println("Открытый ключ: " + pub);
        System.out.println("Закрытый ключ: " + priv);

        System.out.println("\nИсходное сообщение: " + message);

        // Шифрование
        String cipher = RSAEncryptor.encrypt(message, pub);
        System.out.println("Зашифрованное (Base64): " + cipher);

        // Расшифровка
        String decrypted = RSAEncryptor.decrypt(cipher, priv);
        System.out.println("Расшифрованное сообщение: " + decrypted);

        // Подпись
        BigInteger signature = RSASignature.sign(message, priv);
        System.out.println("\nПодпись: " + signature);

        // Проверка
        boolean valid = RSASignature.verify(message, signature, pub);
        System.out.println("Проверка подписи: " + (valid ? "УСПЕХ ✅" : "ОШИБКА ❌"));
    }
}
