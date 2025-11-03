package gost.signature;

import gost.signature.*;
import gost.stribog.Hash;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Базовый тест для проверки формирования и верификации подписи по ГОСТ Р 34.10-2018.
 * Использует тестовые параметры для 256 бит (из curve_params_256.json).
 */
class SignatureTest {

    // --- Тестовые данные (Должны быть загружены из JSON в реальном приложении) ---
    // q = 57896044618658097711785492504343953927082934583725450622380973592137631069619
    private final BigInteger Q = new BigInteger("57896044618658097711785492504343953927082934583725450622380973592137631069619");

    // Инициализация параметров, имитирующая чтение JSON
    private final SignatureParameters params256 = new SignatureParameters(
            256, // digit
            new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564821041"), // p
            BigInteger.valueOf(7), // a
            new BigInteger("43308876546767276905765904595650931995942111794451039583252968842033849580414"), // b
            Q, // m
            Q, // q
            new Point(new BigInteger("2"), new BigInteger("4018974056539037503335449422937059775635739389905545080690979365213431566280")) // P
    );

    // Пример секретного ключа (должен быть 0 < d < q)
    private final BigInteger SECRET_KEY_D = new BigInteger("12345678901234567890123456789012345678901234567890123456789012345678901234567");

    // Тестовое сообщение (массив беззнаковых байтов)
    private final int[] TEST_MESSAGE = {0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65}; // "message"

    /**
     * Тест 1: Проверка полного цикла: Генерация ключа проверки -> Подпись -> Верификация.
     */
    @Test
    void fullSignatureVerificationCycle() throws Exception {
        // 1. Генерация открытого ключа Q = d * P
        EllipticCurve curve = new EllipticCurve(params256);
        Point publicKeyQ = curve.scalar(SECRET_KEY_D, params256.P());

        // 2. Хэширование сообщения
        Hash stribog = new Hash(256);
        BigInteger messageHash = stribog.getHash(TEST_MESSAGE);

        // 3. Формирование подписи
        Sign signer = new Sign();
        String signature = signer.signing(messageHash, SECRET_KEY_D, params256);
        assertNotNull(signature, "Подпись не должна быть null");
        assertEquals(params256.p().bitLength() / 2, signature.length(), "Длина подписи должна быть 2 * (p.bitLength / 4)");

        // 4. Верификация подписи (с правильным ключом)
        Verify verifier = new Verify();
        boolean result = verifier.check(signature, publicKeyQ, messageHash, params256);
        assertTrue(result, "Верификация должна пройти успешно для корректной подписи");
    }

    /**
     * Тест 2: Проверка верификации с неверным сообщением (хэшем).
     */
    @Test
    void verificationFailsWithWrongHash() throws Exception {
        // 1. Генерация ключа Q
        EllipticCurve curve = new EllipticCurve(params256);
        Point publicKeyQ = curve.scalar(SECRET_KEY_D, params256.P());

        // 2. Хэширование и подпись корректного сообщения
        Hash stribog = new Hash(256);
        BigInteger correctHash = stribog.getHash(TEST_MESSAGE);

        Sign signer = new Sign();
        String signature = signer.signing(correctHash, SECRET_KEY_D, params256);

        // 3. Хэширование неверного сообщения
        int[] WRONG_MESSAGE = {0x77, 0x72, 0x6f, 0x6e, 0x67}; // "wrong"
        BigInteger wrongHash = stribog.getHash(WRONG_MESSAGE);

        // 4. Верификация
        Verify verifier = new Verify();
        boolean result = verifier.check(signature, publicKeyQ, wrongHash, params256);
        assertFalse(result, "Верификация должна завершиться неудачей при неверном хэше");
    }
}