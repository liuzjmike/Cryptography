
public abstract class Cipher {
	
	protected final byte[] cipherText;
	protected final static int MASK = 0xff;
	
	protected Cipher(byte[] cipherText) {
		this.cipherText = cipherText;
	}
	
	public int size() {
		return cipherText.length;
	}
	
	public String toString() {
		StringBuffer hex = new StringBuffer();
	    for (int i = 0; i < cipherText.length; i++)
	    {
	        hex.append(Integer.toHexString(cipherText[i]));
	    }
	    return hex.toString();
	}
	
	public abstract String decrypt(byte[] key);
	
	public static byte[] xor(Cipher m1, Cipher m2) {
		return xor(m1.cipherText, m2.cipherText);
	}
	
	static public byte[] xor(byte[] s1, byte[] s2) {
		byte[] ret = new byte[Math.min(s1.length, s2.length)];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = (byte) (s1[i] ^ s2[i]);
		}
		return ret;
	}
}
