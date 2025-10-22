package main.java.rsa.model;

import java.math.BigInteger;

/**
 * Приватный ключ RSA: (d, n)
 */
public final class PrivateKey {
    private final BigInteger d;
    private final BigInteger n;


    public PrivateKey(BigInteger d, BigInteger n) {
        this.d = d;
        this.n = n;
    }


    public BigInteger getD() {
        return d;
    }


    public BigInteger getN() {
        return n;
    }


    @Override
    public String toString() {
        return "PrivateKey{d=" + d + ", n=" + n + "}";
    }
}