package main.java.rsa.model;

import java.math.BigInteger;


/**
 * Публичный ключ RSA: (e, n)
 */
public final class PublicKey {
    private final BigInteger e;
    private final BigInteger n;


    public PublicKey(BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
    }


    public BigInteger getE() {
        return e;
    }


    public BigInteger getN() {
        return n;
    }


    @Override
    public String toString() {
        return "PublicKey{e=" + e + ", n=" + n + "}";
    }
}
