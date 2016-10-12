
public class Message {
	
	private final String cypherText;
	
	public Message(String cypherText) {
		this.cypherText = cypherText;
	}
	
	public int size() {
		return cypherText.length();
	}
	
	public String toString() {
		StringBuffer hex = new StringBuffer();
	    for (int i = 0; i < cypherText.length(); i++)
	    {
	        hex.append(Integer.toHexString((int)cypherText.charAt(i)));
	    }
	    return hex.toString();
	}
	
	public char[] decrypt(String key) {
		return xor(cypherText, key);
	}
	
	static public char[] xor(Message m1, Message m2) {
		return xor(m1.cypherText, m2.cypherText);
	}
	
	static public char[] xor(String s1, String s2) {
		char[] ret = new char[Math.min(s1.length(), s2.length())];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = (char) (s1.charAt(i) ^ s2.charAt(i));
		}
		return ret;
	}
	
	static public void guess(Message m1, Message m2, char[] combi, int[][] key, String phrase) {
		for(int i = 0; i <= combi.length - phrase.length(); i++) {
			boolean correct = true;
			for(int j = 0; j < phrase.length(); j++) {
				char ch = (char) (combi[i+j] ^ phrase.charAt(j));
				if(!((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))) {
					correct = false;
					break;
				}
			}
			if(correct) {
				for(int j = 0; j < phrase.length(); j++) {
					int possibleKey1 = m1.cypherText.charAt(i+j) ^ phrase.charAt(j);
					int possibleKey2 = m2.cypherText.charAt(i+j) ^ phrase.charAt(j);
					key[i][possibleKey1]++;
					key[i][possibleKey2]++;
				}
			}
		}
	}
}
