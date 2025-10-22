package main.java.rsa.core;

import main.java.rsa.model.PrivateKey;
import main.java.rsa.model.PublicKey;
import main.java.rsa.model.RSAKeyPair;

import java.math.BigInteger;
import java.security.SecureRandom;


/**
 * Генерация ключевой пары RSA.
 * По умолчанию используется публичная экспонента e = 65537, если она взаимно простая с phi.
 * Размер ключа указывается в битах (рекомендуется 2048 или больше).
 */
public final class KeyGenerator {
    private static final BigInteger DEFAULT_E = BigInteger.valueOf(65537);
    private final SecureRandom random = new SecureRandom();


    /**
     * Сгенерировать RSAKeyPair с заданной длиной ключа в битах.
     * @param bitLength общий размер модуля n в битах (обычно 1024, 2048, 4096)
     */
    public RSAKeyPair generate(int bitLength) {
        if (bitLength < 512) throw new IllegalArgumentException("bitLength must be >= 512 for security and correctness");


// Генерируем p и q примерно одинаковой длины
        int primeBitLength = bitLength / 2;
        BigInteger p, q;
        do {
            p = BigInteger.probablePrime(primeBitLength, random);
            q = BigInteger.probablePrime(primeBitLength, random);
// гарантируем p != q
        } while (p.equals(q));


        BigInteger n = p.multiply(q);


// phi(n) = (p-1)*(q-1)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));


        BigInteger e = DEFAULT_E;
// если e не взаимно просто с phi, ищем другое e
        if (!e.gcd(phi).equals(BigInteger.ONE)) {
            e = BigInteger.valueOf(3);
            while (!e.gcd(phi).equals(BigInteger.ONE)) {
                e = e.add(BigInteger.TWO);
            }
        }


        BigInteger d = e.modInverse(phi);


        PublicKey pub = new PublicKey(e, n);
        PrivateKey priv = new PrivateKey(d, n);
        return new RSAKeyPair(pub, priv);
    }


    /**
     * Быстрая генерация с разумным значением по умолчанию (2048 бит).
     */
    public RSAKeyPair generateDefault() {
        return generate(2048);
    }

    /**
     * Тестовый метод main для проверки генерации и корректности ключей.
     */
    public static void main(String[] args) {
        KeyGenerator generator = new KeyGenerator();
        RSAKeyPair keyPair = generator.generate(1024);


        System.out.println("=== RSA Key Generation Test ===");
        System.out.println("Public key e: " + keyPair.getPublicKey().getE());
        System.out.println("Public key n: " + keyPair.getPublicKey().getN());
        System.out.println("Private key d: " + keyPair.getPrivateKey().getD());
        System.out.println("Private key n: " + keyPair.getPrivateKey().getN());


        System.out.println("\nДлина модуля n: " + keyPair.getPublicKey().getN().bitLength() + " бит");


    // Проверим корректность шифрования/расшифрования
        BigInteger message = BigInteger.valueOf(123456789);
        BigInteger e = keyPair.getPublicKey().getE();
        BigInteger d = keyPair.getPrivateKey().getD();
        BigInteger n = keyPair.getPublicKey().getN();


        BigInteger encrypted = message.modPow(e, n);
        BigInteger decrypted = encrypted.modPow(d, n);


        System.out.println("\n=== Encryption/Decryption Test ===");
        System.out.println("Original message: " + message);
        System.out.println("Encrypted message: " + encrypted);
        System.out.println("Decrypted message: " + decrypted);


        if (message.equals(decrypted)) {
            System.out.println("\nПроверка успешна ✅ (m == decrypted)");
        } else {
            System.out.println("\nОшибка ❌ (m != decrypted)");
        }


        System.out.println("\nГенерация завершена успешно!");
    }
}