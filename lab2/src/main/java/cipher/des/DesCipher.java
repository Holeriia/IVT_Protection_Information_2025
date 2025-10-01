package main.java.cipher.des;

public class DesCipher implements BlockCipher {
    private final int[][] roundKeys;
    private static final int BLOCK_SIZE = 8; // 64 бита

    public DesCipher(byte[] key) {
        this.roundKeys = KeyScheduler.generateKeys(key);
    }

    @Override
    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    @Override
    public byte[] encryptBlock(byte[] block) {
        return DESCore.encryptBlock(block, roundKeys);
    }

    @Override
    public byte[] decryptBlock(byte[] data64) {
        // Для дешифрования используем обратный порядок ключей
        int[][] reversedKeys = new int[16][];
        for (int i = 0; i < 16; i++) {
            reversedKeys[i] = roundKeys[15 - i];
        }
        return DESCore.encryptBlock(data64, reversedKeys);
    }
}