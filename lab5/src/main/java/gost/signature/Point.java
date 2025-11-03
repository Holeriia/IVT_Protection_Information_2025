package gost.signature;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigInteger;

/**
 * Класс, представляющий точку на эллиптической кривой.
 * Используется для базовой точки P, открытого ключа Q и промежуточных точек вычислений.
 */
public record Point(BigInteger x, BigInteger y) {

    @Override
    public String toString() {
        return "Xp = " + x() + "\nYp = " + y() + "\n";
    }

    /**
     * Конструктор для десериализации JSON с помощью Jackson.
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Point {
        // Конструктор по умолчанию для Record
    }
}