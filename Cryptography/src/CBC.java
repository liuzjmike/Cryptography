import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Random;

public class CBC extends Cipher {
    
    private static final int KEY_LENGTH = 16;
    private static final int BLOCK_SIZE = 16;

    public CBC(byte[] cipherText) {
        super(cipherText);
    }
    
    public static byte[] encrypt(byte[] key, String plainText) {
        byte[] iv = new byte[KEY_LENGTH];
        new Random().nextBytes(iv);
        byte[] plainTextInByte = plainText.getBytes();
        byte[] cipherText = new byte[(plainText.length()/BLOCK_SIZE + 2) * BLOCK_SIZE];
        int i = 0;
        while(i < BLOCK_SIZE) {
            cipherText[i] = iv[i];
            i++;
        }
        byte[] block = iv;
        while(i < plainText.length() - BLOCK_SIZE) {
            block = xor(block, Arrays.copyOfRange(plainTextInByte, i, i + BLOCK_SIZE));
            try {
                block = AES.encrypt(key, "AES", block);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            for(int j = 0; j < BLOCK_SIZE; j++) {
                cipherText[i+j+BLOCK_SIZE] = block[j];
            }
            i += BLOCK_SIZE;
        }
        byte[] lastBlock = new byte[BLOCK_SIZE];
        int j = 0;
        while(i < cipherText.length) {
            if(i < plainTextInByte.length) {
                lastBlock[j] = plainTextInByte[i];
            } else {
                lastBlock[j] = (byte) (cipherText.length - plainTextInByte.length);
            }
            i++;
            j++;
        }
        i -= BLOCK_SIZE;
        lastBlock = xor(block, lastBlock);
        try {
            lastBlock = AES.encrypt(key, "AES", plainTextInByte);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        for(j = 0; j < BLOCK_SIZE; j++) {
            cipherText[i+j+BLOCK_SIZE] = lastBlock[j];
        }
        return cipherText;
    }

    @Override
    public String decrypt(byte[] key) {
        byte[] plainText = new byte[cipherText.length];
        byte[] mask = Arrays.copyOfRange(cipherText, 0, BLOCK_SIZE);
        byte[] masked;
        for(int i = BLOCK_SIZE; i < cipherText.length; i += BLOCK_SIZE) {
            byte[] cipherBlock = Arrays.copyOfRange(cipherText, i, i + BLOCK_SIZE);
            try {
                masked = AES.decrypt(key, "AES/ECB/NoPadding", cipherBlock);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return "";
            }
            masked = xor(mask, masked);
            for(int j = 0; j < BLOCK_SIZE; j++) {
                plainText[i+j] = masked[j];
            }
            mask = cipherBlock;
        }
        return new String(Arrays.copyOfRange(plainText, BLOCK_SIZE, 
                plainText.length - plainText[plainText.length-1]));
    }
    
    public static void main(String[] args) {
        byte[] key = Cipher.readHex("140b41b22a29beb4061bda66b6747e14");
        String cipherText = "4ca00ff4c898d61e1edbf1800618fb2828a226d160dad07883d04e008a7897ee"
                + "2e4b7465d5290d0c0e6c6822236e1daafb94ffe0c5da05d9476be028ad7c1d81";
        CBC test = new CBC(Cipher.readHex(cipherText));
        System.out.println(test.decrypt(key));
    }
}
