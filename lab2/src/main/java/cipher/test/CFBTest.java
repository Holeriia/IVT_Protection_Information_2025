package main.java.cipher.test;

import main.java.cipher.block.BlockCipher;
import main.java.cipher.block.CfbMode;
import main.java.cipher.des.DesCipher;
import main.java.cipher.util.HexUtils;

public class CFBTest {

    private static void runCfbDes(String pt, String key, String iv) {
        CfbMode cfb = new CfbMode(new DesCipher(HexUtils.hexStringToByteArray(key)),
                HexUtils.hexStringToByteArray(iv));

        byte[] cipher    = cfb.encrypt(HexUtils.hexStringToByteArray(pt));
        byte[] decrypted = cfb.decrypt(cipher);

        String cipherHex    = HexUtils.byteArrayToHexString(cipher);
        String decryptedHex = HexUtils.byteArrayToHexString(decrypted);

        System.out.printf("""
                ------------- CFB-DES -------------
                Открытый текст: %s
                Ключ:           %s
                IV:             %s
                Шифртекст:      %s
                Расшифровка:    %s
                %s

                """,
                pt, key, iv, cipherHex, decryptedHex,
                decryptedHex.equals(pt) ? "✅ УСПЕХ" : "❌ ОШИБКА"
        );
    }

    public static void main(String[] args) {
        runCfbDes("0123456789ABCDEF", "133457799BBCDFF1", "1234567890ABCDEF");
        runCfbDes("0011223344556677", "AABB09182736CCDD", "0F0E0D0C0B0A0908");
    }
    
}
