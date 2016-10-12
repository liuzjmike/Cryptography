import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ManyTimePadBreaker {
	
	public String start(String filename, int target) {
		List<StreamCipher> sampleTexts;
		try {
			sampleTexts = readFile(filename);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		byte[] key = findKey(sampleTexts);
		return new String(sampleTexts.get(target).decrypt(key));
	}
	
	private byte[] findKey(List<StreamCipher> sampleTexts) {
		int len = 0;
		for(int i = 0; i < sampleTexts.size(); i++) {
			if(sampleTexts.get(i).size() > len) {
				len = sampleTexts.get(i).size();
			}
		}
		int[][] key = new int[len][256];
		for(int i = 0; i < sampleTexts.size()-1; i++) {
			for(int j = 0; j < sampleTexts.size(); j++) {
				StreamCipher m1 = sampleTexts.get(i);
				StreamCipher m2 = sampleTexts.get(j);
				StreamCipher.guessKey(m1, m2, StreamCipher.xor(m1, m2), key, " the ");
				StreamCipher.guessKey(m1, m2, StreamCipher.xor(m1, m2), key, " ");
			}
		}
		byte[] ret = new byte[len];
		for(int i = 0; i < key.length; i++) {
			int dex = 0;
			for(int j = 0; j < key[0].length; j++) {
				if(key[i][j] > key[i][dex]) {
					dex = j;
				}
			}
			ret[i] = (byte)dex;
		}
		return ret;
	}
	
	/**
	 * Reads a file containing all the sample cipher texts and returns
	 * a list containing all the sample cipher texts
	 * @param filename
	 *             the name of the file containing all the sample cipher
	 *             texts, one in each line, all encrypted with the same
	 *             key using stream cipher
	 * @return
	 * @throws IOException
	 */
	private List<StreamCipher> readFile(String filename) throws IOException {
		FileInputStream fstream = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		ArrayList<StreamCipher> sampleTexts = new ArrayList<StreamCipher>();
		String line;
		while((line = br.readLine()) != null) {
			sampleTexts.add(new StreamCipher(readHex(line)));
		}
		br.close();
		return sampleTexts;
	}
	
	/**
	 * Reads a string of hexadecimal numbers and returns a byte[]
	 * containing the corresponding value
	 * @param hex
	 * @return
	 */
	private byte[] readHex(String hex) {
	    if(hex.length()%2 != 0) {
	        throw new IllegalArgumentException();
	    }
	    byte[] cypherText = new byte[hex.length()/2];
		for(int i = 0; i < hex.length()-1; i += 2) {
			cypherText[i/2] = (byte)Integer.parseInt(hex.substring(i,i+2), 16);
		}
		return cypherText;
	}

	public static void main(String[] args) {
		String filename = "data/CypherTexts.txt";
		ManyTimePadBreaker test = new ManyTimePadBreaker();
		System.out.println(test.start(filename, 10));
	}
}
