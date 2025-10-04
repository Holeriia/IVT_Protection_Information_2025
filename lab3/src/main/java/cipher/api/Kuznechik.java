package main.java.cipher.api;

import main.java.cipher.kuznechik.KuznechikCipher;

/**
 * Адаптер для работы с KuznechikCipher через интерфейс BlockCipher.
 */
public class Kuznechik implements BlockCipher {

    private final KuznechikCipher cipher;

    /**
     * Конструктор принимает 32-байтовый ключ (256 бит).
     * Первые 16 байт — key1, вторые 16 — key2.
     */
    public Kuznechik(byte[] fullKey) {
        if (fullKey.length != 32) {
            throw new IllegalArgumentException("Ключ Кузнечика должен быть длиной 32 байта (256 бит)");
        }
        byte[] key1 = new byte[16];
        byte[] key2 = new byte[16];
        System.arraycopy(fullKey, 0, key1, 0, 16);
        System.arraycopy(fullKey, 16, key2, 0, 16);
        this.cipher = new KuznechikCipher(key1, key2);
    }

    @Override
    public int blockSize() {
        return 16; // размер блока Кузнечика — 128 бит
    }

    @Override
    public byte[] encrypt(byte[] block) {
        if (block.length != 16) {
            throw new IllegalArgumentException("Размер блока должен быть 16 байт");
        }
        return cipher.encryptBlock(block);
    }

    @Override
    public byte[] decrypt(byte[] block) {
        if (block.length != 16) {
            throw new IllegalArgumentException("Размер блока должен быть 16 байт");
        }
        return cipher.decryptBlock(block);
    }
}
