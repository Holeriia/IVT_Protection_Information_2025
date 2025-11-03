package gost.signature;

import gost.occasion.AlienExceptions;
import java.math.BigInteger;

/**
 * Класс для выполнения основных операций над точками эллиптической кривой:
 * сложения и скалярного умножения, используемых в ГОСТ Р 34.10-2018.
 */
public class EllipticCurve {
    private final SignatureParameters parameters;
    private final BigInteger p; // Модуль поля (простое число)

    public EllipticCurve (SignatureParameters parameters) {
        this.parameters = parameters;
        this.p = parameters.p();
    }

    private BigInteger modP(BigInteger x) {
        if (x == null) return null;

        // Используем remainder для получения остатка.
        BigInteger result = x.remainder(p);

        // Гарантируем, что результат находится в диапазоне [0, p-1].
        if (result.signum() < 0) {
            result = result.add(p);
        }
        return result;
    }

    /**
     * Выполняет сложение двух точек эллиптической кривой (Point A + Point B).
     * @param A Первая точка.
     * @param B Вторая точка.
     * @return Результирующая точка C.
     */
    public Point sum (Point A, Point B) throws AlienExceptions.IncorrectParametersException {
        // Обработка нейтрального элемента (Точка в бесконечности)
        if (A.x() == null) return B;
        if (B.x() == null) return A;

        BigInteger x1 = modP(A.x());
        BigInteger y1 = modP(A.y());
        BigInteger x2 = modP(B.x());
        BigInteger y2 = modP(B.y());
        BigInteger lambda;
        BigInteger a_mod_p = modP(parameters.a());

        // 1. Удвоение (x1 == x2 и y1 == y2)
        if (x1.equals(x2) && y1.equals(y2)) {
            // lambda = (3*x1^2 + a) * (2*y1)^(-1) mod p

            // Числитель: (3*x1^2 + a)
            BigInteger numerator = x1.modPow(BigInteger.TWO, p)
                    .multiply(BigInteger.valueOf(3))
                    .add(a_mod_p);
            numerator = modP(numerator);

            // Знаменатель: 2*y1 mod p
            BigInteger denominator = y1.shiftLeft(1).mod(p);

            if (denominator.equals(BigInteger.ZERO)) {
                return new Point(null, null); // Точка в бесконечности
            }

            lambda = numerator.multiply(denominator.modInverse(p)).mod(p);

            // 2. Симметричные точки (Точка в бесконечности)
        } else if (x1.equals(x2) && y1.add(y2).mod(p).equals(BigInteger.ZERO)) {
            return new Point(null, null);

            // 3. Обычное сложение (x1 != x2)
        } else {
            // lambda = (y2 - y1) * (x2 - x1)^(-1) mod p

            BigInteger dy = modP(y2.subtract(y1));
            BigInteger dx = modP(x2.subtract(x1));

            if (dx.equals(BigInteger.ZERO)) {
                throw new AlienExceptions.IncorrectParametersException();
            }

            lambda = dy.multiply(dx.modInverse(p)).mod(p);
        }

        // Единый расчет X3 и Y3
        // Xc = lambda^2 - x1 - x2 mod p
        BigInteger lambda_sq_mod_p = lambda.modPow(BigInteger.TWO, p);
        BigInteger Xc_raw = lambda_sq_mod_p.subtract(x1).subtract(x2);
        BigInteger x3 = modP(Xc_raw);

        // Yc = lambda * (x1 - Xc) - y1 mod p
        BigInteger Yc_raw = lambda.multiply(x1.subtract(x3)).subtract(y1);
        BigInteger y3 = modP(Yc_raw);

        return new Point(x3, y3);
    }

    /**
     * Скалярное умножение: Horner (MSB-first).
     * @param k Скаляр (целое число, например, секретный ключ d).
     * @param point Точка, которую нужно умножить (например, базовая точка P).
     * @return Результирующая точка.
     */
    public Point scalar (BigInteger k, Point point) throws AlienExceptions.IncorrectParametersException {
        if (k == null) return new Point(null, null);

        // Приведём k к диапазону [0, q-1]
        BigInteger q = parameters.q();
        k = k.mod(q);

        if (k.signum() == 0) return new Point(null, null);

        // Нормализуем базовую точку
        Point base = point;
        if (point.x() != null && point.y() != null) {
            base = new Point(modP(point.x()), modP(point.y()));
        }

        Point result = new Point(null, null); // Нейтральный элемент O
        for (int i = k.bitLength() - 1; i >= 0; i--) {
            // Удвоение
            result = sum(result, result);
            if (k.testBit(i)) {
                // Сложение с базовой точкой
                result = sum(result, base);
            }
        }
        return result;
    }
}