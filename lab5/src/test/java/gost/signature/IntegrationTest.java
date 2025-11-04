package gost.signature;

import gost.stribog.Hash;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты для проверки краевых условий и границ ключей в ECC ГОСТ.
 */
class IntegrationTest {

    // --- Тестовые данные (из SignatureTest) ---
    private final BigInteger Q = new BigInteger("57896044618658097711785492504343953927082934583725450622380973592137631069619");
    private final SignatureParameters params256 = new SignatureParameters(
            256, // digit
            new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564821041"), // p
            BigInteger.valueOf(7), // a
            new BigInteger("43308876546767276905765904595650931995942111794451039583252968842033849580414"), // b
            Q, // m
            Q, // q
            new Point(new BigInteger("2"), new BigInteger("4018974056539037503335449422937059775635739389905545080690979365213431566280")) // P
    );
    private final int[] TEST_MESSAGE = {0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65}; // "message"
    private final BigInteger messageHash;
    private final EllipticCurve curve;

    public IntegrationTest() throws Exception {
        Hash stribog = new Hash(256);
        messageHash = stribog.getHash(TEST_MESSAGE);
        curve = new EllipticCurve(params256);
    }

    /**
     * Вспомогательный метод, выполняющий полный цикл Подпись -> Верификация для заданного ключа d.
     */
    private boolean runFullCycle(BigInteger d) throws Exception {
        // 1. Генерация Q = d * P
        Point publicKeyQ = curve.scalar(d, params256.P());

        // 2. Подпись
        Sign signer = new Sign();
        String signature = signer.signing(messageHash, d, params256);

        // 3. Верификация
        Verify verifier = new Verify();
        return verifier.check(signature, publicKeyQ, messageHash, params256);
    }

    /**
     * Тест 1: Проверка с минимально возможным секретным ключом: d = 1.
     */
    @Test
    void testBoundaryMinKeyD() throws Exception {
        // Минимально допустимый ключ d = 1
        BigInteger dMin = BigInteger.ONE;
        assertTrue(runFullCycle(dMin), "Верификация должна пройти для d = 1");
    }

    /**
     * Тест 2: Проверка с максимально возможным секретным ключом: d = q - 1.
     */
    @Test
    void testBoundaryMaxKeyD() throws Exception {
        // Максимально допустимый ключ d = q - 1
        BigInteger dMax = params256.q().subtract(BigInteger.ONE);
        assertTrue(runFullCycle(dMax), "Верификация должна пройти для d = q - 1");
    }

    /**
     * Тест 3: Проверка на невалидный секретный ключ (должен вызвать ошибку в MessageManager,
     * но здесь мы проверяем, что если бы он попал в Sign, то не сработал бы).
     * NOTE: Sign.signing не проверяет d, MessageManager это делает. Здесь мы проверяем
     * верификацию, используя публичный ключ, сгенерированный из d=q.
     */
    @Test
    void testInvalidKeyQCheck() throws Exception {
        // d = q (недопустимо по ГОСТ)
        BigInteger dInvalid = params256.q();

        // 1. Генерируем Q = d_invalid * P
        Point publicKeyQ = curve.scalar(dInvalid, params256.P());

        // 2. Генерируем подпись на правильном ключе (d=1)
        BigInteger dCorrect = BigInteger.ONE;
        Sign signer = new Sign();
        String signature = signer.signing(messageHash, dCorrect, params256);

        // 3. Пытаемся проверить подпись (от d=1) ключом Q (от d=q). Должно провалиться.
        Verify verifier = new Verify();
        boolean result = verifier.check(signature, publicKeyQ, messageHash, params256);

        assertFalse(result, "Верификация должна провалиться, если Q не соответствует d, использованному для подписи.");
    }
}