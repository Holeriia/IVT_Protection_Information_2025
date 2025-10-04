package main.java.cipher.kuznechik;

public class RoundKeys {
    private final byte[][] keys; // 10 ключей по 16 байт

    public RoundKeys(int rounds, int blockSize) {
        keys = new byte[rounds][blockSize];
    }

    public void setKey(int round, byte[] key) {
        if (key.length != keys[0].length)
            throw new IllegalArgumentException("Неправильный размер ключа");
        keys[round] = key.clone();
    }

    public byte[] getKey(int round) {
        return keys[round].clone();
    }

    public int size() {
        return keys.length;
    }
}