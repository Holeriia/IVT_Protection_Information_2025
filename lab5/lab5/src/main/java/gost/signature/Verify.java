package gost.signature;

import gost.occasion.AlienExceptions;

import java.math.BigInteger;

/**
 * Класс, реализующий процесс проверки ЭЦП по ГОСТ Р 34.10-2018.
 */
public class Verify {
    private SignatureParameters parameters;

    /**
     * Основной метод для проверки ЭЦП.
     * @param sign Подпись в формате (r || s) в hex-строке.
     * @param Q Публичный ключ (точка на кривой).
     * @param hash Хэш сообщения.
     * @param para Параметры кривой.
     * @return true, если подпись верна; false в противном случае.
     */
    public boolean check (String sign, Point Q, BigInteger hash, SignatureParameters para) throws Exception {
        this.parameters = para;
        BigInteger r, s;

        // Шаг 1: Извлечение r и s из подписи
        try {
            int partLength = parameters.p().bitLength() / 4;
            r = new BigInteger(sign.substring(0, partLength), 16);
            s = new BigInteger(sign.substring(partLength), 16);
        }
        catch (StringIndexOutOfBoundsException | NumberFormatException e) {
            throw new AlienExceptions.SignatureUnreadableException();
        }

        // Шаг 2: Проверка 0 < r < q и 0 < s < q
        var q = parameters.q();
        if (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(q) >= 0 ||
                s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(q) >= 0) {
            return false;
        }

        // Шаги 3-4: Вычисление e и v = e^(-1) mod q
        BigInteger e = calcE(hash);
        BigInteger v = e.modInverse(q);

        // Шаг 5: Вычисление z1 и z2
        // z1 = s * v mod q
        BigInteger z1 = (s.multiply(v)).mod(q);
        // z2 = (-r) * v mod q
        BigInteger z2 = (r.negate().multiply(v)).mod(q);

        // Шаг 6: Вычисление точки C' = z1*P + z2*Q
        var curveOperation = new EllipticCurve(parameters);
        Point P1 = curveOperation.scalar(z1, parameters.P());
        Point P2 = curveOperation.scalar(z2, Q);

        Point C_prime = curveOperation.sum(P1, P2);

        // Проверка на точку в бесконечности
        if (C_prime.x() == null) return false;

        // Шаг 7: R = x_C' mod q. Проверка: R == r
        BigInteger R = C_prime.x().mod(q);

        return R.compareTo(r) == 0;
    }

    // Шаги 3: e = H(m) mod q. Если e=0, то e=1.
    private BigInteger calcE(BigInteger hash) {
        var e = hash.mod(parameters.q());
        if (e.compareTo(BigInteger.ZERO) == 0)
            e = BigInteger.ONE;
        return e;
    }
}