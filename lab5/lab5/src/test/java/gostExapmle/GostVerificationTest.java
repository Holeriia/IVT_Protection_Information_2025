package gostExapmle;

import gost.occasion.AlienExceptions;
import gost.signature.EllipticCurve;
import gost.signature.Point;
import gost.signature.SignatureParameters;
import gost.signature.Verify;
import gost.stribog.Hash;
import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест-векторы ГОСТ Р 34.10-2018 (Примеры А.2 и А.3).
 * Проверяет корректность параметров, хеширования и логики верификации,
 * используя верифицированные константы из GostTestParameters.
 */
class GostVerificationTest {

    // --- Инициализация объектов для работы ---
    // Кривые инициализируются корректными параметрами из GostTestParameters
    private final EllipticCurve curve256 = new EllipticCurve(GostTestParameters.PARAMS_256);
    private final EllipticCurve curve512 = new EllipticCurve(GostTestParameters.PARAMS_512);
    private final Verify verifier = new Verify();
    private final Hash stribog256 = new Hash(GostTestParameters.SIZE_256);
    private final Hash stribog512 = new Hash(GostTestParameters.SIZE_512);


//    // --- Вспомогательный метод для проверки ---
//    private void runVerificationTest(
//            EllipticCurve curve, SignatureParameters params,
//            int hashSize, BigInteger controlD, BigInteger controlE,
//            String controlSignature, Point controlQ) throws Exception
//    {
//        // 1. ПРОВЕРКА ПАРАМЕТРОВ КРИВОЙ: Q должно быть равно d * P
//        // Если этот шаг не проходит, проблема в EllipticCurve.sum/scalar.
//        Point calculatedQ = curve.scalar(controlD, params.P());
//        assertEquals(controlQ, calculatedQ,
//                "[" + hashSize + " бит] Ошибка в параметрах кривой: Контрольный публичный ключ Q не совпадает с D * P. Проверьте EllipticCurve."
//        );
//
//        // 2. ПРОВЕРКА ХЕШИРОВАНИЯ: e должно совпадать
//        // Если этот шаг не проходит, проблема в Stribog (ГОСТ 34.11-2018).
//        BigInteger calculatedE = (hashSize == 256)
//                ? stribog256.getHash(GostTestParameters.MESSAGE_M_BYTES)
//                : stribog512.getHash(GostTestParameters.MESSAGE_M_BYTES);
//
//        assertEquals(controlE, calculatedE,
//                "[" + hashSize + " бит] Ошибка в хеш-функции: Хеш-код E сообщения не совпадает с контрольным. Проверьте Stribog."
//        );
//
//        // 3. ВЕРИФИКАЦИЯ: Проверка логики
//        // Если этот шаг не проходит, проблема в логике Sign/Verify, но не в кривой или хеше.
//        boolean result = verifier.check(controlSignature, controlQ, calculatedE, params);
//
//        assertTrue(result,
//                "[" + hashSize + " бит] Верификация НЕ прошла с контрольной подписью. Ошибка в логике Verify.check."
//        );
//    }
// --- Вспомогательный метод для проверки с детальным выводом ---
private void runVerificationTest(
        EllipticCurve curve, SignatureParameters params,
        int hashSize, BigInteger controlD, BigInteger controlE,
        String controlSignature, Point controlQ) throws Exception
{
    System.out.println("--- Детализация теста " + hashSize + " бит (ГОСТ А.2/А.3) ---");

    // 1. ПРОВЕРКА ПАРАМЕТРОВ КРИВОЙ: Q должно быть равно d * P
    Point calculatedQ = curve.scalar(controlD, params.P());

    System.out.println("P (Базовая точка): X=" + params.P().x().toString(10));
    System.out.println("D (Секретный ключ): " + controlD.toString(16));
    System.out.println("-----------------------------------------------------------------------");

    // Вывод фактического и ожидаемого Q
    System.out.println("ОЖИДАЕМЫЙ Q (Из ГОСТ):");
    System.out.println("Xq (Exp): " + controlQ.x().toString(10));
    System.out.println("Yq (Exp): " + controlQ.y().toString(10));

    System.out.println("-----------------------------------------------------------------------");

    System.out.println("РАССЧИТАННЫЙ Q (D * P):");
    System.out.println("Xq (Act): " + calculatedQ.x().toString(10));
    System.out.println("Yq (Act): " + calculatedQ.y().toString(10));

    System.out.println("--- Результат проверки Q = D * P ---");
    assertEquals(controlQ, calculatedQ,
            "[" + hashSize + " бит] Ошибка в параметрах кривой: Контрольный публичный ключ Q не совпадает с D * P. Проверьте EllipticCurve."
    );

    // 2. ПРОВЕРКА ХЕШИРОВАНИЯ: e должно совпадать
    BigInteger calculatedE = (hashSize == 256)
            ? stribog256.getHash(GostTestParameters.MESSAGE_M_BYTES)
            : stribog512.getHash(GostTestParameters.MESSAGE_M_BYTES);

    assertEquals(controlE, calculatedE,
            "[" + hashSize + " бит] Ошибка в хеш-функции: Хеш-код E сообщения не совпадает с контрольным. Проверьте Stribog."
    );

    // 3. ВЕРИФИКАЦИЯ: Проверка логики
    boolean result = verifier.check(controlSignature, controlQ, calculatedE, params);

    assertTrue(result,
            "[" + hashSize + " бит] Верификация НЕ прошла с контрольной подписью. Ошибка в логике Verify.check."
    );
}

    // -------------------------------------------------------------------------
    //                              ТЕСТЫ
    // -------------------------------------------------------------------------

    @Test
    void testControlVector256BitVerification() throws Exception {
        runVerificationTest(
                curve256, GostTestParameters.PARAMS_256,
                GostTestParameters.SIZE_256, GostTestParameters.D256_SECRET,
                GostTestParameters.E256_HASH, GostTestParameters.SIGNATURE_256,
                GostTestParameters.Q256_PUBLIC
        );
        System.out.println("256-битный контрольный вектор верификации успешно пройден.");
    }

    @Test
    void testControlVector512BitVerification() throws Exception {
        runVerificationTest(
                curve512, GostTestParameters.PARAMS_512,
                GostTestParameters.SIZE_512, GostTestParameters.D512_SECRET,
                GostTestParameters.E512_HASH, GostTestParameters.SIGNATURE_512,
                GostTestParameters.Q512_PUBLIC
        );
        System.out.println("512-битный контрольный вектор верификации успешно пройден.");
    }
}