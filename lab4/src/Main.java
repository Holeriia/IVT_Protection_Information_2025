import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import main.java.rsa.core.KeyGenerator;
import main.java.rsa.model.RSAKeyPair;
import main.java.rsa.model.PublicKey;
import main.java.rsa.model.PrivateKey;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== RSA Лабораторная ===");

        // Генерация ключей
        KeyGenerator generator = new KeyGenerator();
        RSAKeyPair keyPair = generator.generate(1024);
        PublicKey pub = keyPair.getPublicKey();
        PrivateKey priv = keyPair.getPrivateKey();

        System.out.println("Открытый ключ: " + pub);
        System.out.println("Закрытый ключ: " + priv);

        // Исходное сообщение
        String message = "Привет, RSA!";
        System.out.println("\nИсходное сообщение: " + message);

        // Шифрование
        BigInteger m = new BigInteger(1, message.getBytes(StandardCharsets.UTF_8)); // ✅ положительное
        BigInteger c = m.modPow(pub.getE(), pub.getN());
        System.out.println("Зашифрованное (число): " + c);
        String cipherBase64 = Base64.getEncoder().encodeToString(c.toByteArray());
        System.out.println("Зашифрованное (Base64): " + cipherBase64);

        // Расшифрование
        BigInteger decrypted = c.modPow(priv.getD(), priv.getN());
        byte[] decryptedBytes = decrypted.toByteArray();

        // Возможен ведущий ноль (0x00) → удаляем, если есть
        if (decryptedBytes[0] == 0) {
            byte[] tmp = new byte[decryptedBytes.length - 1];
            System.arraycopy(decryptedBytes, 1, tmp, 0, tmp.length);
            decryptedBytes = tmp;
        }

        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
        System.out.println("Расшифрованное сообщение: " + decryptedText);

        // Подпись
        BigInteger signature = m.modPow(priv.getD(), priv.getN());
        System.out.println("\nПодпись: " + signature);

        // Проверка подписи
        BigInteger verified = signature.modPow(pub.getE(), pub.getN());
        boolean valid = verified.equals(m);
        System.out.println("Проверка подписи: " + (valid ? "УСПЕХ ✅" : "ОШИБКА ❌"));
    }
}
