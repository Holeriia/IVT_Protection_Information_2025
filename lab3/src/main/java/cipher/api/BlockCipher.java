package main.java.cipher.api;

/**
 * Интерфейс блочного шифра.
 * Любой алгоритм (DES, Kuznechik и др.) должен реализовать эти методы,
 * чтобы использоваться в режимах работы (CFB).
 */
public interface BlockCipher {
    // Размер блока в байтах
    int getBlockSize();

    // Шифрование одного блока
    byte[] encryptBlock(byte[] block);

    // Расшифрование одного блока
    byte[] decryptBlock(byte[] block);
}
