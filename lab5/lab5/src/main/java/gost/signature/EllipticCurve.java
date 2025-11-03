package gost.signature;

import gost.occasion.AlienExceptions;

import java.math.BigInteger;

/**
 * Класс для выполнения основных операций над точками эллиптической кривой:
 * сложения и скалярного умножения, используемых в ГОСТ Р 34.10-2018.
 */
public class EllipticCurve {
    private final SignatureParameters parameters;
    private final BigInteger p; // Модуль поля

    public EllipticCurve (SignatureParameters parameters) {
        this.parameters = parameters;
        this.p = parameters.p();
    }

    /**
     * Реализует скалярное умножение точки на число (k * Point).
     * Используется алгоритм удвоения-сложения (double-and-add).
     * @param k Скаляр (целое число, например, секретный ключ d или случайное k).
     * @param point Точка, которую нужно умножить (например, базовая точка P).
     * @return Результирующая точка.
     */
    public Point scalar (BigInteger k, Point point) throws AlienExceptions.IncorrectParametersException {
        // Установка нейтрального элемента (точка в бесконечности)
        var result = new Point(null, null);

        // Обходим биты k от старшего к младшему
        for (int i = k.bitLength() - 1; i >= 0; i--){
            // 1. Удвоение: result = result + result
            try {
                result = sum(result, result);
            } catch (AlienExceptions.IncorrectParametersException e) {
                // Преобразуем ошибку в более общую, если нужно, или просто пробрасываем
                throw new AlienExceptions.IncorrectParametersException();
            }

            // 2. Сложение, если бит равен 1: result = result + point
            if (k.testBit(i))
                result = sum(result, point);
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
        // Проверка на нейтральный элемент (точку в бесконечности)
        if (B.x() == null) return A;
        if (A.x() == null) return B;

        BigInteger x, y;
        BigInteger lambda;

        try {
            if (B.equals(A)) {
                // Удвоение точки A (A = B)
                // lambda = (3*x^2 + a) * (2*y)^(-1) mod p
                BigInteger numerator = (A.x().pow(2).multiply(BigInteger.valueOf(3))).add(parameters.a());
                BigInteger denominator = A.y().multiply(BigInteger.TWO);
                lambda = numerator.multiply(denominator.modInverse(p)).mod(p);
            } else if (A.x().compareTo(B.x()) == 0) {
                // A.x == B.x, A.y == -B.y (A = -B), результат - точка в бесконечности
                return new Point(null, null);
            } else {
                // Сложение двух разных точек (A != B)
                // lambda = (By - Ay) * (Bx - Ax)^(-1) mod p
                BigInteger numerator = B.y().subtract(A.y());
                BigInteger denominator = B.x().subtract(A.x());
                lambda = numerator.multiply(denominator.modInverse(p)).mod(p);
            }

            // Xc = lambda^2 - Ax - Bx mod p
            x = (lambda.modPow(BigInteger.TWO, p).subtract(A.x()).subtract(B.x()).mod(p));

            // Yc = lambda * (Ax - Xc) - Ay mod p
            y = (A.y().negate().mod(p)).add(lambda.multiply(A.x().subtract(x))).mod(p);

        } catch (ArithmeticException e) {
            // Ошибка при modInverse (например, знаменатель равен 0)
            throw new AlienExceptions.IncorrectParametersException();
        }

        return new Point(x, y);
    }
}