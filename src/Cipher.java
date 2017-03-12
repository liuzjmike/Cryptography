
public abstract class Cipher {
	
	protected final byte[] cipherText;
	protected final static int MASK = 0xff;
	
	protected Cipher(byte[] cipherText) {
		this.cipherText = cipherText;
	}
	
	protected Cipher(String cipherText) {
	    this.cipherText = readHex(cipherText);
	}
	
	public int size() {
		return cipherText.length;
	}
	
	public String toString() {
	    return toHexString(cipherText);
	}
	
	public abstract String decrypt(byte[] key);
	
	public static byte[] xor(Cipher m1, Cipher m2) {
		return xor(m1.cipherText, m2.cipherText);
	}
	
	public static byte[] xor(byte[] s1, byte[] s2) {
		byte[] ret = new byte[Math.min(s1.length, s2.length)];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = (byte) (s1[i] ^ s2[i]);
		}
		return ret;
	}
	
	public static String toHexString(byte[] byteSequence) {
	    StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteSequence.length; i++)
        {
            hexString.append(Integer.toHexString((byteSequence[i] & MASK) + 0x100).substring(1));
        }
        return hexString.toString();
	}
    
    /**
     * Reads a string of hexadecimal numbers and returns a byte[]
     * containing the corresponding value
     * @param hex
     * @return
     */
    public static byte[] readHex(String hex) {
        if(hex.length()%2 != 0) {
            throw new IllegalArgumentException();
        }
        byte[] cypherText = new byte[hex.length()/2];
        for(int i = 0; i < hex.length()-1; i += 2) {
            cypherText[i/2] = (byte)Integer.parseInt(hex.substring(i,i+2), 16);
        }
        return cypherText;
    }
}
