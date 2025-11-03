package gost.stribog;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Реализация хэш-функции Стрибог (ГОСТ Р 34.11-2018).
 * Поддерживает режимы 256 и 512 бит.
 */
public class Hash {
    private final boolean is512Bit; // true для 512 бит, false для 256 бит
    private final int[] iv = new int[64]; // Начальное значение H (H0)
    private int[] N = new int[64]; // Счетчик длины
    private int[] sig = new int[64]; // Суммарный хэш сообщения

    private final StribogConstants stribogConstants = new StribogConstants();

    /**
     * Инициализация хэш-функции.
     * @param hashLength Длина хэша в битах (256 или 512).
     */
    public Hash(int hashLength) {
        if (hashLength != 256 && hashLength != 512) {
            throw new IllegalArgumentException("Поддерживаются только длины хэша 256 или 512 бит.");
        }
        this.is512Bit = (hashLength == 512);

        // Установка H0 (iv)
        int a = this.is512Bit ? 0x00 : 0x01;
        Arrays.fill(N, 0x00);
        Arrays.fill(sig, 0x00);
        Arrays.fill(iv, a);
    }

    /**
     * Основной метод для вычисления хэша сообщения.
     * @param message Сообщение в виде массива байтов (int[] для беззнаковых байтов).
     * @return Хэш сообщения в виде BigInteger.
     */
    public BigInteger getHash(int[] message) {
        var h = Arrays.copyOf(iv, 64);
        var messageLength = message.length;
        var offset = 0;

        // 1. Обработка блоков по 64 байта
        while (messageLength >= 64) {
            var m = Arrays.copyOfRange(message, offset, offset + 64);
            h = gN(h, m, N);

            // Обновление N: N = N + 512
            N = ringAdd(N, stribogConstants.numByte(512 / 8));

            // Обновление sig: sig = sig + m
            sig = ringAdd(sig, m);

            messageLength -= 64;
            offset += 64;
        }

        // 2. Добивка последнего блока (Шаг 3.1)
        var lastBlock = new int[64];
        if (messageLength > 0) {
            System.arraycopy(message, offset, lastBlock, 0, messageLength);
        }
        // Установка 0x01 после сообщения
        lastBlock[messageLength] = 0x01;

        // 3. Обработка последнего блока
        h = gN(h, lastBlock, N);

        // 4. Обновление N и sig
        // Обновление N: N = N + (длина последнего блока)
        N = ringAdd(N, stribogConstants.numByte(messageLength));
        // Обновление sig: sig = sig + (последний блок)
        sig = ringAdd(sig, lastBlock);

        // 5. Финальные преобразования (Шаги 4 и 5)
        h = gN(h, N, stribogConstants.numByte(0));
        h = gN(h, sig, stribogConstants.numByte(0));

        // 6. Формирование результата
        if (is512Bit) {
            return new BigInteger(1, int2byte(h));
        } else {
            // Для 256 бит берем только первые 32 байта
            var h256 = Arrays.copyOf(h, 32);
            return new BigInteger(1, int2byte(h256));
        }
    }

    // --- Вспомогательные функции Стрибога ---

    /**
     * Функция сжатия (Compression function).
     */
    private int[] gN(int[] h, int[] m, int[] val) {
        var LPS = L(P(S(xFun(h, val))));
        return xFun(xFun(E(LPS, m), h), m);
    }

    /**
     * Функция зацикленной итерации (Key schedule and iteration).
     */
    private int[] E(int[] k, int[] m) {
        var result = xFun(k, m);
        var tempKey = k;
        for (var i = 0; i < 12; i++) {
            tempKey = L(P(S(xFun(tempKey, stribogConstants.C[i]))));
            result = xFun(L(P(S(result))), tempKey);
        }
        return result;
    }

    /**
     * Побитовое исключающее ИЛИ (XOR).
     */
    private int[] xFun(int[] k, int[] a) {
        var result = new int[k.length];
        for (var i = 0; i < k.length; i++) result[i] = (k[i] ^ a[i]);
        return result;
    }

    /**
     * Операция подстановки (Substitution S).
     */
    private int[] S (int[] val) {
        var result = new int[64];
        for (var i = 0; i < 64; i++)
            result[i] = stribogConstants.substitution[val[i] & 0xFF]; // & 0xFF для безопасности
        return result;
    }

    /**
     * Операция перестановки (Permutation P).
     */
    private int[] P (int[] val) {
        var result = new int[64];
        for (var i = 0; i < 64; i++)
            result[i] = val[stribogConstants.t[i]];
        return result;
    }

    /**
     * Линейное преобразование (Linear transformation L).
     */
    private int[] L(int[] val) {
        var result = new int[64];
        var x = new int[8];
        var y = new int[8];

        for (var j = 0; j < 8; j++) {
            // Разделение 64 байт на 8 векторов по 8 байт
            System.arraycopy(val, j * 8, x, 0, 8);

            // Выполнение линейного преобразования
            for (var k = 0; k < 8; k++) {
                y[k] = 0;
                for (var i = 0; i < 8; i++) {
                    var byteToXor = x[i];
                    for (var l = 0; l < 8; l++) {
                        if (((stribogConstants.A[k][i] >> l) & 0x01) == 1) {
                            y[k] ^= byteToXor;
                        }
                        byteToXor = (byteToXor << 1) | ((byteToXor >> 7) & 0x01); // Циклический сдвиг влево
                    }
                }
            }
            // Запись результата
            System.arraycopy(y, 0, result, j * 8, 8);
        }
        return result;
    }

    /**
     * Сложение в кольце Z/2^512Z (с переносом, переполнение отбрасывается).
     */
    private int[] ringAdd(int[] a, int[] b) {
        var c = new int[64];
        var carry = 0;
        for (var i = 63; i >= 0; i--) {
            // Сложение двух чисел и перенос из предыдущей операции
            var temp = a[i] + b[i] + (carry >> 8);
            c[i] = temp & 0xff; // Сохраняем младший байт
            carry = temp;       // Перенос - это вся сумма
        }
        return c;
    }

    /**
     * Преобразует массив беззнаковых int (0-255) в массив signed byte.
     */
    private byte[] int2byte(int[] input) {
        var dataOut = new byte[input.length];
        for (var i = 0; i < input.length; i++) {
            dataOut[i] = (byte) input[i];
        }
        return dataOut;
    }
}