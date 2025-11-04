package gost.signature;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Класс, реализующий процесс формирования ЭЦП по ГОСТ Р 34.10-2018.
 */
public class Sign {
    private SignatureParameters parameters;
    private BigInteger k;
    private BigInteger e;
    private BigInteger d; // Секретный ключ
    private BigInteger r;
    private BigInteger s;

    private EllipticCurve curveOperation;

    /**
     * Основной метод для формирования ЭЦП.
     * @param hash Хэш сообщения (результат работы Стрибога).
     * @param d Секретный ключ.
     * @param parameters Параметры кривой.
     * @return Подпись в виде конкатенированной строки (r || s).
     */
    public String signing (BigInteger hash, BigInteger d, SignatureParameters parameters) throws Exception {
        this.parameters = parameters;
        curveOperation = new EllipticCurve(parameters);
        this.d = d;

        // Шаги 2-3: Вычисление e
        calcE(hash);

        // Шаги 4-7: Генерация k, вычисление r и s (с рекурсией)
        randK();

        // Шаг 8: Формирование и конкатенация подписи
        return concatenation();
    }

    // Шаги 2-3: e = H(m) mod q. Если e=0, то e=1.
    private void calcE(BigInteger hash) {
        e = hash.mod(parameters.q());
        if (e.equals(BigInteger.ZERO))
            e = BigInteger.ONE;
    }

    // Шаг 4: Генерация криптостойкого псевдослучайного числа k (0 < k < q).
    private void randK() throws Exception {
        var rand = new SecureRandom();
        var q = parameters.q();

        k = new BigInteger(q.bitLength(), rand);
        while (k.compareTo(q) >= 0 || k.compareTo(BigInteger.ZERO) < 1)
            k = new BigInteger(q.bitLength(), rand);

        // Переходим к Шагу 5
        genC();
    }

    // Шаг 5: Вычисление точки C = k * P
    private void genC() throws Exception {
        var pnt = curveOperation.scalar(k, parameters.P());

        // Переходим к Шагу 6
        calcR(pnt);
    }

    // Шаг 6: r = x_C mod q
    private void calcR(Point C) throws Exception {
        r = C.x().mod(parameters.q());
        if (r.equals(BigInteger.ZERO))
            randK(); // Если r=0, повторяем Шаг 4

        // Переходим к Шагу 7
        calcS();
    }

    // Шаг 7: s = (r*d + k*e) mod q
    private void calcS() throws Exception {
        s = ((r.multiply(d)).add(k.multiply(e))).mod(parameters.q());
        if (s.equals(BigInteger.ZERO))
            randK(); // Если s=0, повторяем Шаг 4
    }

    // Вспомогательный метод для дополнения r или s нулями до требуемой длины
    private String completion(BigInteger num) {
        int targetLength = (parameters.q().bitLength() + 3) / 4; // ceil(bits/4)
        var str = new StringBuilder(num.toString(16));
        while (str.length() < targetLength) str.insert(0, "0");
        return str.toString();
    }

    private String concatenation() {
        return completion(r) + completion(s);
    }
}