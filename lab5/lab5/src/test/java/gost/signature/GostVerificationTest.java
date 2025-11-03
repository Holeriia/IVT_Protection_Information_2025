package gost.signature;

import gost.signature.*;
import gost.stribog.Hash;
import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест-векторы ГОСТ Р 34.10-2018 (Примеры 1 и 2).
 * Проверяет корректность параметров, хеширования и логики верификации.
 */
class GostVerificationTest {

    // --- Загрузка параметров (имитация) ---
    // В реальном коде вам нужно реализовать загрузку из вашего JSON-файла
    private final SignatureParameters params256 = createParameters256();
    private final SignatureParameters params512 = createParameters512();

    // --- Инициализация объектов для работы ---
    private final EllipticCurve curve256 = new EllipticCurve(params256);
    private final EllipticCurve curve512 = new EllipticCurve(params512);
    private final Verify verifier = new Verify();
    private final Hash stribog256 = new Hash(GostControlVectors.SIZE_256);
    private final Hash stribog512 = new Hash(GostControlVectors.SIZE_512);

    // --- Вспомогательный метод для проверки ---
    private void runVerificationTest(
            EllipticCurve curve, SignatureParameters params,
            int hashSize, BigInteger controlD, BigInteger controlE,
            String controlSignature, Point controlQ) throws Exception
    {
        // 1. ПРОВЕРКА ПАРАМЕТРОВ КРИВОЙ: Q должно быть равно d * P
        Point calculatedQ = curve.scalar(controlD, params.P());
        assertEquals(controlQ, calculatedQ,
                "[" + hashSize + " бит] Ошибка в параметрах кривой: Контрольный публичный ключ Q не совпадает с D * P. Проверьте p, a, b, q, P."
        );

        // 2. ПРОВЕРКА ХЕШИРОВАНИЯ: e должно совпадать
        // Используем байты, так как ваш Stribog, вероятно, принимает их.
        BigInteger calculatedE = (hashSize == 256)
                ? stribog256.getHash(GostControlVectors.TEST_MESSAGE_INT_ARRAY)
                : stribog512.getHash(GostControlVectors.TEST_MESSAGE_INT_ARRAY);

        assertEquals(controlE, calculatedE,
                "[" + hashSize + " бит] Ошибка в хеш-функции: Хеш-код e сообщения не совпадает с контрольным. Проверьте Stribog."
        );

        // 3. ВЕРИФИКАЦИЯ: Проверка логики
        boolean result = verifier.check(controlSignature, controlQ, calculatedE, params);

        assertTrue(result,
                "[" + hashSize + " бит] Верификация НЕ прошла с контрольной подписью. Ошибка в логике Verify.check или в параметрах."
        );
    }

    // -------------------------------------------------------------------------
    //                              ТЕСТЫ
    // -------------------------------------------------------------------------

    @Test
    void testControlVector256BitVerification() throws Exception {
        runVerificationTest(
                curve256, params256,
                GostControlVectors.SIZE_256, GostControlVectors.D256,
                GostControlVectors.E256, GostControlVectors.SIGNATURE_256,
                GostControlVectors.Q256
        );
        System.out.println("256-битный контрольный вектор верификации успешно пройден.");
    }

    @Test
    void testControlVector512BitVerification() throws Exception {
        runVerificationTest(
                curve512, params512,
                GostControlVectors.SIZE_512, GostControlVectors.D512,
                GostControlVectors.E512, GostControlVectors.SIGNATURE_512,
                GostControlVectors.Q512
        );
        System.out.println("512-битный контрольный вектор верификации успешно пройден.");
    }

    // -------------------------------------------------------------------------
    //                       ИНИЦИАЛИЗАЦИЯ ПАРАМЕТРОВ ИЗ JSON
    // -------------------------------------------------------------------------

    private SignatureParameters createParameters256() {
        BigInteger p = new BigInteger("8000000000000000000000000000000000000000000000000000000000000431", 16);
        BigInteger a = BigInteger.valueOf(7);
        BigInteger b = new BigInteger("5FBFF498AA938CE739B8E022FBAFEF40563F6E6A3472FC2A514C0CE9DAE23B7E", 16);
        BigInteger q = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF6C611070995AD10045841B09B761B893", 16);
        Point P = new Point(
                new BigInteger("2", 16),
                new BigInteger("8E2A8A0E65147D8FAA6626F0C3B9C1CF198E9393920D483A7260B8BB3BBBAFDA", 16)
        );
        return new SignatureParameters(256, p, a, b, q, q, P);
    }


    private SignatureParameters createParameters512() {
        try {
            // Инициализация из предоставленных JSON-данных
            BigInteger p = new BigInteger("3623986102229003635907788753683874306021320925534678605086546150450856166624002482588482022271496854025090823603058735163734263822371964987228582907372403");
            BigInteger a = BigInteger.valueOf(7);
            BigInteger b = new BigInteger("1518655069210828534508950034714043154928747527740206436194018823352809982443793732829756914785974674866041605397883677596626326413990136959047435811826396");
            BigInteger q = new BigInteger("3623986102229003635907788753683874306021320925534678605086546150450856166623969164898305032863068499961404079437936585455865192212970734808812618120619743");
            Point P = new Point(
                    new BigInteger("1928356944067022849399309401243137598997786635459507974357075491307766592685835441065557681003184874819658004903212332884252335830250729527632383493573274"),
                    new BigInteger("2288728693371972859970012155529478416353562327329506180314497425931102860301572814141997072271708807066593850650334152381857347798885864807605098724013854")
            );
            return new SignatureParameters(512, p, a, b, q, q, P);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при инициализации параметров 512: " + e.getMessage());
        }
    }
}