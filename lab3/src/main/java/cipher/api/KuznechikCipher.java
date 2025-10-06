package main.java.cipher.api;

import main.java.cipher.kuznechik.KuznechikCore;

import java.nio.charset.StandardCharsets;

/**
 * Адаптер для работы с алгоритмом Кузнечик через интерфейс BlockCipher.
 */
public class KuznechikCipher implements BlockCipher {

    private final KuznechikCore cipher;

    /**
     * Конструктор принимает 32-байтовый ключ (256 бит)
     * и делит его на две половины по 16 байт.
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

    /** Возвращает размер блока — 16 байт (128 бит). */
    @Override
    public int getBlockSize() {
        return 16;
    }

    /** Шифрует один 16-байтовый блок данных. */
    @Override
    public byte[] encryptBlock(byte[] block) {
        if (block.length != 16) {
            throw new IllegalArgumentException("Размер блока должен быть 16 байт");
        }
        return cipher.encryptBlock(block);
    }

    /** Расшифровывает один 16-байтовый блок данных. */
    @Override
    public byte[] decryptBlock(byte[] block) {
        if (block.length != 16) {
            throw new IllegalArgumentException("Размер блока должен быть 16 байт");
        }
        return cipher.decryptBlock(block);
    }
}