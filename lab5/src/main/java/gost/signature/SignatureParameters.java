package gost.signature;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigInteger;

/**
 * Класс, содержащий базовые параметры эллиптической кривой
 * для ГОСТ Р 34.10-2018.
 */
public record SignatureParameters (
        Integer digit, // Размер хэша (256 или 512 бит)
        BigInteger p,  // Модуль поля (характеристика)
        BigInteger a,  // Коэффициент a кривой
        BigInteger b,  // Коэффициент b кривой
        BigInteger m,  // Порядок группы точек
        BigInteger q,  // Порядок циклической подгруппы (главный модуль)
        Point P        // Базовая точка-генератор
) {
    @Override
    public String toString() {
        return "Параметры кривой:\n" +
                "p = " + p + "\n" +
                "a = " + a + "\n" +
                "b = " + b + "\n" +
                "m = " + m + "\n" +
                "q = " + q + "\n" +
                "Точка P:\n" + P.toString();
    }

    /**a
     * Конструктор для десериализации JSON с помощью Jackson.
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public SignatureParameters {
        // Конструктор по умолчанию для Record
    }
}