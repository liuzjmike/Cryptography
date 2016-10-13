import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    public static byte[] encrypt(byte[] key, String transformation, byte[] plainText)
            throws GeneralSecurityException {
        Cipher cipher = initializeCipher(key, transformation, Cipher.ENCRYPT_MODE);
        return cipher.doFinal(plainText);
    }
    
    public static byte[] decrypt(byte[] key, String transformation, byte[] cipherText)
            throws GeneralSecurityException {
        Cipher cipher = initializeCipher(key, transformation, Cipher.DECRYPT_MODE);
        return cipher.doFinal(cipherText);
    }
    
    private static Cipher initializeCipher(byte[] key, String transformation, int opmode)
            throws GeneralSecurityException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(opmode, keySpec);
        return cipher;
    }
}
