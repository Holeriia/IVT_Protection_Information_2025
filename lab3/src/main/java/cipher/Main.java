package main.java.cipher;

import main.java.cipher.kuznechik.RoundKeys;

public class Main {
    public static void main(String[] args) {
        RoundKeys rk = new RoundKeys(10, 16);

        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) key[i] = (byte) i;

        rk.setKey(0, key);

        byte[] k = rk.getKey(0);
        System.out.print("Ключ раунда 0: ");
        for (byte b : k) System.out.printf("%02X ", b);
    }
}