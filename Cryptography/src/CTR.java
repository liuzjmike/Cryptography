import java.security.GeneralSecurityException;
import java.util.Arrays;

public class CTR extends Cipher {

    private static final int BLOCK_SIZE = 16;
    
    public CTR(byte[] cipherText) {
        super(cipherText);
    }
    
    public CTR(String cipherText) {
        super(cipherText);
    }

    public static byte[] encrypt(byte[] key, String plainText) {
        byte[] iv = AES.generateIV(BLOCK_SIZE);
        byte[] plainTextInByte = plainText.getBytes();
        byte[] cipherText = new byte[plainTextInByte.length + BLOCK_SIZE];
        int i = 0;
        while(i < BLOCK_SIZE) {
            cipherText[i] = iv[i];
            i++;
        }
        byte[] cipherBlock;
        while(i < cipherText.length) {
            try {
                cipherBlock = AES.encrypt(key, iv);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return cipherText;
            }
            if(i > cipherText.length - BLOCK_SIZE) {
                cipherBlock = Cipher.xor(Arrays.copyOfRange(plainTextInByte, i - BLOCK_SIZE, 
                        plainTextInByte.length), cipherBlock);
                for(int j = 0; j < cipherText.length - i; j++) {
                    cipherText[i+j] = cipherBlock[j];
                }
            } else {
                cipherBlock = Cipher.xor(Arrays.copyOfRange(plainTextInByte, i - BLOCK_SIZE, i), cipherBlock);
                for(int j = 0; j < BLOCK_SIZE; j++) {
                    cipherText[i+j] = cipherBlock[j];
                }
            }
            i += BLOCK_SIZE;
            incrementByteArray(iv);
        }
        return cipherText;
    }
    
    @Override
    public String decrypt(byte[] key) {
        byte[] plainText = new byte[cipherText.length - BLOCK_SIZE];
        byte[] iv = Arrays.copyOfRange(cipherText, 0, BLOCK_SIZE);
        byte[] plain;
        for(int i = BLOCK_SIZE; i < cipherText.length; i += BLOCK_SIZE) {
            try {
                plain = AES.encrypt(key, iv);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return new String(plainText);
            }
            if(i > cipherText.length - BLOCK_SIZE) {
                plain = Cipher.xor(Arrays.copyOfRange(cipherText, i, cipherText.length), plain);
                for(int j = 0; j < cipherText.length - i; j++) {
                    plainText[i+j-BLOCK_SIZE] = plain[j];
                }
            } else {
                plain = Cipher.xor(Arrays.copyOfRange(cipherText, i, i + BLOCK_SIZE), plain);
                for(int j = 0; j < BLOCK_SIZE; j++) {
                    plainText[i+j-BLOCK_SIZE] = plain[j];
                }
            }
            incrementByteArray(iv);
        }
        try {
            plain = AES.encrypt(key, iv);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return new String(plainText);
        }
        return new String(plainText);
    }

    private static void incrementByteArray(byte[] a) {
        byte msb = a[0];
        a[a.length-1]++;
        for(int i = a.length-1; i > 0; i--) {
            if(a[i] == 0) {
                a[i-1]++;
            } else {
                break;
            }
        }
        if(msb != a[0] && a[0] == 0) {
            a[a.length-1]++;
        }
    }
    
    public static void main(String[] args) {
        byte[] key = Cipher.readHex("36f18357be4dbd77f050515c73fcf9f2");
        String plainText = "Always avoid the two time pad!";
        //String cipherText = "770b80259ec33beb2561358a9f2dc617e46218c0a53"
        //        + "cbeca695ae45faa8952aa0e311bde9d4e01726d3184c34451";
        CTR test = new CTR(encrypt(key, plainText));
        //CTR test = new CTR(readHex(cipherText));
        System.out.println(test.decrypt(key));
    }
}
