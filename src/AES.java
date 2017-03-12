import java.security.GeneralSecurityException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    
    public static final String TRANSFORMATION = "AES/ECB/NoPadding";
    
    public static byte[] generateIV(int blockSize) {
        byte[] iv = new byte[blockSize];
        new Random().nextBytes(iv);
        return iv;
    }
    
    public static byte[] encrypt(byte[] key, byte[] plainText)
            throws GeneralSecurityException {
        Cipher cipher = initializeCipher(key, Cipher.ENCRYPT_MODE);
        return cipher.doFinal(plainText);
    }
    
    public static byte[] decrypt(byte[] key, byte[] cipherText)
            throws GeneralSecurityException {
        Cipher cipher = initializeCipher(key, Cipher.DECRYPT_MODE);
        return cipher.doFinal(cipherText);
    }
    
    private static Cipher initializeCipher(byte[] key, int opmode)
            throws GeneralSecurityException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(opmode, keySpec);
        return cipher;
    }
}
