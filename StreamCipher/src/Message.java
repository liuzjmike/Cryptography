
public class Message {
	
	private final byte[] cypherText;
	private final static int MASK = 0xff;
	
	public Message(byte[] cypherText) {
		this.cypherText = cypherText;
	}
	
	public int size() {
		return cypherText.length;
	}
	
	public String toString() {
		StringBuffer hex = new StringBuffer();
	    for (int i = 0; i < cypherText.length; i++)
	    {
	        hex.append(Integer.toHexString(cypherText[i]));
	    }
	    return hex.toString();
	}
	
	public String decrypt(byte[] key) {
		return new String(xor(cypherText, key));
	}
	
	static public byte[] xor(Message m1, Message m2) {
		return xor(m1.cypherText, m2.cypherText);
	}
	
	static public byte[] xor(byte[] s1, byte[] s2) {
		byte[] ret = new byte[Math.min(s1.length, s2.length)];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = (byte) (s1[i] ^ s2[i]);
		}
		return ret;
	}
	
	static public void guess(Message m1, Message m2, byte[] combi, int[][] key, String phrase) {
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
					int possibleKey1 = (m1.cypherText[i+j] & MASK) ^ phrase.charAt(j);
					int possibleKey2 = (m2.cypherText[i+j] & MASK) ^ phrase.charAt(j);
					key[i][possibleKey1]++;
					key[i][possibleKey2]++;
				}
			}
		}
	}
}
