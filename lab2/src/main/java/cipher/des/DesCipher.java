package main.java.cipher.des;

import main.java.cipher.block.BlockCipher;

/**
 * Реализация блочного шифра DES (64 бита = 8 байт).
 * Работает с одним блоком данных.
 */
public class DesCipher implements BlockCipher {
    private final int[][] roundKeys;          // 16 подключей, сгенерированных из ключа
    private static final int BLOCK_SIZE = 8;  // размер блока в байтах

    //Конструктор: принимает ключ (8 байт) и сразу генерирует подключи
    public DesCipher(byte[] key) {
        this.roundKeys = KeyScheduler.generateKeys(key);
    }

    //Возвращает размер блока (8 байт)
    @Override
    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    //Шифрует один блок (64 бита)
    @Override
    public byte[] encryptBlock(byte[] block) {
        return DESCore.encryptBlock(block, roundKeys);
    }

    //Расшифровывает один блок (64 бита) — ключи идут в обратном порядке
    @Override
    public byte[] decryptBlock(byte[] data64) {
        int[][] reversedKeys = new int[16][];
        for (int i = 0; i < 16; i++) {
            reversedKeys[i] = roundKeys[15 - i];
        }
        return DESCore.encryptBlock(data64, reversedKeys);
    }
}