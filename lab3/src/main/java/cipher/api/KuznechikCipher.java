package main.java.cipher.api;

import main.java.cipher.kuznechik.KuznechikCore;

import java.nio.charset.StandardCharsets;

/**
 * Адаптер для работы с KuznechikCipher через интерфейс BlockCipher.
 */
public class KuznechikCipher implements BlockCipher {

    private final KuznechikCore cipher;

    /**
     * Конструктор принимает 32-байтовый ключ (256 бит).
     * Первые 16 байт — key1, вторые 16 — key2.
     */
    public KuznechikCipher(byte[] fullKey) {
        if (fullKey.length != 32) {
            throw new IllegalArgumentException("Ключ Кузнечика должен быть длиной 32 байта (256 бит)");
        }
        byte[] key1 = new byte[16];
        byte[] key2 = new byte[16];
        System.arraycopy(fullKey, 0, key1, 0, 16);
        System.arraycopy(fullKey, 16, key2, 0, 16);
        this.cipher = new KuznechikCore(key1, key2);
    }

    @Override
    public int getBlockSize() {
        return 16; // размер блока Кузнечика — 128 бит
    }

    @Override
    public byte[] encryptBlock(byte[] block) {
        if (block.length != 16) {
            throw new IllegalArgumentException("Размер блока должен быть 16 байт");
        }
        return cipher.encryptBlock(block);
    }

    @Override
    public byte[] decryptBlock(byte[] block) {
        if (block.length != 16) {
            throw new IllegalArgumentException("Размер блока должен быть 16 байт");
        }
        return cipher.decryptBlock(block);
    }
}
