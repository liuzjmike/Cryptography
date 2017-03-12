import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaddingOracle {
    
    private static final String TARGET = "http://crypto-class.appspot.com/po?er=";
    private static final int BLOCK_SIZE = 16;
    
    public static String getMessage(String cipher) {
        byte[] cipherText = Cipher.readHex(cipher);
        byte[] answer = new byte[cipherText.length - BLOCK_SIZE];
        for(int i = answer.length-1; i >= 0; i--) {
            getByte(cipherText, answer, i);
        }
        return new String(answer);
    }
    
    private static void getByte(byte[] cipherText, byte[] answer, int index) {
        byte padding = (byte) (BLOCK_SIZE - index % BLOCK_SIZE);
        int runningLength = (index / BLOCK_SIZE + 2) * BLOCK_SIZE;
        byte[] trial = new byte[runningLength];
        for(int i = 0; i < runningLength; i++) {
            if(index < i && i < runningLength - BLOCK_SIZE) {
                trial[i] = (byte) (cipherText[i] ^ answer[i] ^ padding);
            } else {
                trial[i] = cipherText[i];
            }
        }
        answer[index] = guessByte(trial, padding, index);
    }
    
    private static byte guessByte(byte[] trial, byte padding, int index) {
        byte target = trial[index];
        for(int guess = 0; guess < 256; guess++) {
            trial[index] = (byte) (target ^ guess ^ padding);
            if(guess == padding) {
                continue;
            }
            if(query(Cipher.toHexString(trial)) == true) {
                return (byte) guess;
            }
        }
        return padding;
    }
    
    private static boolean query(String q) {
        String target = TARGET + q;
        try {
            URL url = new URL(target);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            int response = huc.getResponseCode();
            if(response == HttpURLConnection.HTTP_NOT_FOUND) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static void main(String[] args) {
        System.out.println(getMessage("f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4"));
    }
}
