package main.java.cipher.block;

public interface BlockCipher {
    int getBlockSize();
    byte[] encryptBlock(byte[] block);
    byte[] decryptBlock(byte[] block);
}
