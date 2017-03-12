
public class StreamCipher extends Cipher {
    
    public StreamCipher(byte[] cipherText) {
        super(cipherText);
    }
    
    @Override
    public String decrypt(byte[] key) {
        return new String(xor(cipherText, key));
    }
    
    static public void guessKey(Cipher m1, Cipher m2, byte[] combi, int[][] key, String phrase) {
        for(int i = 0; i <= combi.length - phrase.length(); i++) {
            boolean correct = true;
            for(int j = 0; j < phrase.length(); j++) {
                byte ch = (byte) (combi[i+j] ^ phrase.charAt(j));
                if(!((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))) {
                    correct = false;
                    break;
                }
            }
            if(correct) {
                for(int j = 0; j < phrase.length(); j++) {
                    int possibleKey1 = (m1.cipherText[i+j] ^ phrase.charAt(j)) & MASK;
                    int possibleKey2 = (m2.cipherText[i+j] ^ phrase.charAt(j)) & MASK;
                    key[i][possibleKey1]++;
                    key[i][possibleKey2]++;
                }
            }
        }
    }
}
