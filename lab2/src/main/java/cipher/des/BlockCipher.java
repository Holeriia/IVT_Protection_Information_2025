package main.java.cipher.des;

public interface BlockCipher {
    int getBlockSize();
    byte[] encryptBlock(byte[] block);
    byte[] decryptBlock(byte[] block);
}
