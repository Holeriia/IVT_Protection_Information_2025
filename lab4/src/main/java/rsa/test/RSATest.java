package main.java.rsa.test;

import main.java.rsa.core.KeyGenerator;
import main.java.rsa.core.RSAEncryptor;
import main.java.rsa.core.RSASignature;
import main.java.rsa.model.PrivateKey;
import main.java.rsa.model.PublicKey;
import main.java.rsa.model.RSAKeyPair;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;
import java.math.BigInteger;

public class RSATest {

    public static void main(String[] args) throws Exception {
        String message = "Это тестовое сообщение для сравнения реализаций RSA.";

        System.out.println("=== СРАВНЕНИЕ РЕАЛИЗАЦИЙ RSA ===\n");

        // Наша реализация
        KeyGenerator generator = new KeyGenerator();
        RSAKeyPair customPair = generator.generate(1024);
        PublicKey customPub = customPair.getPublicKey();
        PrivateKey customPriv = customPair.getPrivateKey();

        System.out.println("=== Наша реализация RSA ===");
        System.out.println("Открытый ключ:  e=" + customPub.getE() + "\n n=" + customPub.getN());
        System.out.println("Закрытый ключ:  d=" + customPriv.getD());
        System.out.println("Сообщение:      " + message);

        String encryptedCustom = RSAEncryptor.encrypt(message, customPub);
        String decryptedCustom = RSAEncryptor.decrypt(encryptedCustom, customPriv);

        System.out.println("Зашифровано (Base64): " + encryptedCustom);
        System.out.println("Расшифровано:         " + decryptedCustom);
        System.out.println("Совпадает ли сообщение: " + message.equals(decryptedCustom));

        BigInteger signature = RSASignature.sign(message, customPriv);
        boolean verified = RSASignature.verify(message, signature, customPub);
        System.out.println("Подпись: " + signature);
        System.out.println("Проверка подписи: " + (verified ? "УСПЕХ ✅" : "ОШИБКА ❌"));
        System.out.println();

        // Стандартная реализация Java Security
        System.out.println("=== Стандартная реализация RSA из Java Security ===");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair javaPair = keyGen.generateKeyPair();

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, javaPair.getPublic());
        byte[] encryptedJava = cipher.doFinal(message.getBytes());
        String encodedJava = Base64.getEncoder().encodeToString(encryptedJava);

        cipher.init(Cipher.DECRYPT_MODE, javaPair.getPrivate());
        String decryptedJava = new String(cipher.doFinal(encryptedJava));

        System.out.println("Зашифровано (Base64): " + encodedJava);
        System.out.println("Расшифровано:         " + decryptedJava);
        System.out.println("Совпадает ли сообщение: " + message.equals(decryptedJava));

        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(javaPair.getPrivate());
        sign.update(message.getBytes());
        byte[] javaSignature = sign.sign();

        System.out.println("Подпись: " + Base64.getEncoder().encodeToString(javaSignature));

        sign.initVerify(javaPair.getPublic());
        sign.update(message.getBytes());
        boolean verifiedJava = sign.verify(javaSignature);

        System.out.println("Проверка подписи: " + (verifiedJava ? "УСПЕХ ✅" : "ОШИБКА ❌"));

        // Проверка на подмену данных
        sign.initVerify(javaPair.getPublic());
        sign.update("Поддельное сообщение".getBytes());
        boolean fakeCheck = sign.verify(javaSignature);

        System.out.println("\nПроверка подписи при подмене данных: " + (fakeCheck ? "НЕИСПРАВНО ❌" : "ОТКЛОНЕНО ✅"));

        System.out.println("\n✅ Все проверки завершены.");
    }
}
