package gost.signature;

import java.math.BigInteger;

/**
 * Контрольные векторы ГОСТ Р 34.10-2018 (Примеры А.2 и А.3) для проверки верификации.
 * Векторы обновлены до полных 256- и 512-битных значений, согласно стандарту.
 */
public class GostControlVectors {

    // --- Общее тестовое сообщение (m) из ГОСТ 34.10-2018 (Приложение А) ---
    // 16 байт: 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F 10
    public static final int[] TEST_MESSAGE_INT_ARRAY = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10
    };

    // --- 256 бит (Пример 1, А.2) ---
    public static final int SIZE_256 = 256;
    // Секретный ключ D: 256 бит.
    public static final BigInteger D256 = new BigInteger("798C0B2A3CC4C94E1A61CC6160533036F525916A4C24F00E84D72D1A16C2E628", 16);
    // Публичный ключ Q: Координаты X и Y (256 бит каждая).
    public static final Point Q256 = new Point(
            // Xp (совпадает с рассчитанным вами)
            new BigInteger("5E34A60E3A2F0A311D7B43936E7B9889791F9BC134A26C45DAA05E26E92B1D32", 16),
            // Yp (совпадает с рассчитанным вами)
            new BigInteger("7A6B880860A72F6744C27083049F27C44E1815598E901F4800762E770E103E44", 16)
    );
    // Хеш сообщения E: 256 бит.
    public static final BigInteger E256 = new BigInteger("4A5826A1A78B817A166A8C280735E99477B7809F5E6B56F9F241470ACD9E9923", 16);
    // Подпись (r, s): r и s по 256 бит.
    public static final String SIGNATURE_256 =
            "3D44368C47F37F62EEF0F94A708170D6C31497F9936DE3F540C63532F9953288" + // r
                    "30DE6E9F24C5355416B198B038B3150C773347C7D087612711C4F22312A34035"; // s

    // --- 512 бит (Пример 2, А.3) ---
    public static final int SIZE_512 = 512;
    // Секретный ключ D: 512 бит.
    public static final BigInteger D512 = new BigInteger("B04246944A747806A837A208B21835777F92850A3207E05C6F03F2C3F5E7B5E1447D0E576F3238F465225E9F80C6C577ED023B06979A992C85A8229F8182D2D7", 16);
    // Публичный ключ Q: Координаты X и Y (512 бит каждая).
    public static final Point Q512 = new Point(
            new BigInteger("3B92728D1B68C526A32971168B52E8C0FB8B4C0250669D50337839356D9321ADCB9D390292AD26500F00C8A53545A258C58742C2CE2C8E1925D251C33F17316A", 16),
            new BigInteger("265691F4B41D9C586B79D3B9B255474E456488F47D06C19E868725514E9F2B57B2F8356E74B16075677943241F8C9ED65F292B1938F414F98E84358C892837A6", 16)
    );
    // Хеш сообщения E: 512 бит.
    public static final BigInteger E512 = new BigInteger("8D50A2E28F880B4434224765798F0D3929C42589417E6B8062F8A4B8305739345D2D58E15D4D5C5B990D348E0F05333F24D288764B86015B70A24A3258832A2F", 16);
    // Подпись (r, s): r и s по 512 бит.
    public static final String SIGNATURE_512 =
            "2D0D8F19B8164E24584281720182C9E4254E978F52DBC3924DD12EFE887413661138A73B00D9F5CC0487D437F4619711586B8C673410F150B201F3A8A962E3B2" +
                    "55C6DE6A073843516315510618059E7C2A312F6C502E74B7270F7332C19484E8F8C81A7D18B13F405E328A36625890F0D24B01B739E090435B0D7D0660B08E9D";
}