package main.java.cipher.util;

public class XorUtils {

    // Побайтное XOR двух массивов одинаковой длины
    public static byte[] xor(byte[] a, byte[] b) {
        if (a.length != b.length)
            throw new IllegalArgumentException("Длины массивов должны совпадать");
        byte[] c = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = (byte) (a[i] ^ b[i]);
        }
        return c;
    }
}