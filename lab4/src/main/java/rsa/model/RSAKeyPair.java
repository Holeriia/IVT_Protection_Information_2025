package main.java.rsa.model;

/**
 * Container для пары RSA-ключей
 */
public final class RSAKeyPair {
    private final PublicKey publicKey;
    private final PrivateKey privateKey;


    public RSAKeyPair(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }


    public PublicKey getPublicKey() {
        return publicKey;
    }


    public PrivateKey getPrivateKey() {
        return privateKey;
    }


    @Override
    public String toString() {
        return "RSAKeyPair{public=" + publicKey + ", private=" + privateKey + "}";
    }
}
