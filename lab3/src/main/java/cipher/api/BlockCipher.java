package main.java.cipher.api;

/**
 * Интерфейс блочного шифра.
 */
public interface BlockCipher {
    // Размер блока в байтах
    int getBlockSize();

    // Шифрование одного блока
    byte[] encryptBlock(byte[] block);

    // Расшифрование одного блока
    byte[] decryptBlock(byte[] block);
}
