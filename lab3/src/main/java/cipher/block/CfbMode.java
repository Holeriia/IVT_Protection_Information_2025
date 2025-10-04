package main.java.cipher.block;

import main.java.cipher.api.BlockCipher;

import java.util.Arrays;

/**
 * Режим обратной связи по шифру (CFB).
 * Работает поверх любого блочного шифра (например, DES).
 */
public class CfbMode {
    private final BlockCipher cipher;
    private final int blockSize;
    private byte[] iv;   // начальный вектор

    public CfbMode(BlockCipher cipher, byte[] iv) {
        if (iv.length != cipher.getBlockSize()) {
            throw new IllegalArgumentException("Длина IV должна совпадать с размером блока!");
        }
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.iv = Arrays.copyOf(iv, blockSize);
    }

    /**
     * Шифрование в режиме CFB
     */
    public byte[] encrypt(byte[] plaintext) {
        byte[] output = new byte[plaintext.length];
        byte[] feedback = Arrays.copyOf(iv, blockSize);

        for (int i = 0; i < plaintext.length; i += blockSize) {
            // Шифруем feedback
            byte[] encryptedFeedback = cipher.encryptBlock(feedback);

            // XOR: plaintext ⊕ keystream
            for (int j = 0; j < blockSize && i + j < plaintext.length; j++) {
                output[i + j] = (byte) (plaintext[i + j] ^ encryptedFeedback[j]);
            }

            // Сдвигаем feedback = ciphertext
            feedback = Arrays.copyOfRange(output, i, Math.min(i + blockSize, output.length));
        }
        return output;
    }

    /**
     * Дешифрование в режиме CFB
     */
    public byte[] decrypt(byte[] ciphertext) {
        byte[] output = new byte[ciphertext.length];
        byte[] feedback = Arrays.copyOf(iv, blockSize);

        for (int i = 0; i < ciphertext.length; i += blockSize) {
            // Шифруем feedback
            byte[] encryptedFeedback = cipher.encryptBlock(feedback);

            // XOR: ciphertext ⊕ keystream
            for (int j = 0; j < blockSize && i + j < ciphertext.length; j++) {
                output[i + j] = (byte) (ciphertext[i + j] ^ encryptedFeedback[j]);
            }

            // Сдвигаем feedback = ciphertext
            feedback = Arrays.copyOfRange(ciphertext, i, Math.min(i + blockSize, ciphertext.length));
        }
        return output;
    }
}