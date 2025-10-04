package main.java.cipher.api;

public interface BlockCipher {
    int blockSize();              // возвращает размер блока в байтах
    byte[] encrypt(byte[] block); // шифрование блока
    byte[] decrypt(byte[] block); // расшифрование блока
}
