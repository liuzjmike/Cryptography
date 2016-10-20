import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SHA256 {

    private static final int OUTPUT_SIZE = 32;
    private static final int BLOCK_SIZE = 1024;
    
    public static String signFile(String sourcePath, String targetPath)
            throws IOException, NoSuchAlgorithmException {
        RandomAccessFile raf = new RandomAccessFile(sourcePath, "rw");
        int lastBlock = (int) raf.length() % BLOCK_SIZE;
        Node currentBlock = new Node(lastBlock);
        raf.seek(raf.length() - lastBlock);
        raf.read(currentBlock.block);
        for(long i = raf.length() - lastBlock - BLOCK_SIZE; i >= 0; i -= BLOCK_SIZE) {
            Node newBlock = new Node(BLOCK_SIZE + OUTPUT_SIZE);
            raf.seek(i);
            raf.read(newBlock.block, 0, BLOCK_SIZE);
            byte[] hash = getHash(currentBlock.block);
            for(int j = 0; j < OUTPUT_SIZE; j++) {
                newBlock.block[j + BLOCK_SIZE] = hash[j];
            }
            newBlock.next = currentBlock;
            currentBlock = newBlock;
        }
        raf.close();
        byte[] h0 = getHash(currentBlock.block);
        writeFile(currentBlock, targetPath);
        return Cipher.toHexString(h0);
    }

    public static boolean verify(String pathname, String h0)
            throws IOException, NoSuchAlgorithmException {
        File f = new File(pathname);
        FileInputStream fis = new FileInputStream(f);
        long numBlock = f.length() / (BLOCK_SIZE + OUTPUT_SIZE);
        int lastBlock = (int) f.length() % (BLOCK_SIZE + OUTPUT_SIZE);
        byte[] currentBlock = new byte[BLOCK_SIZE + OUTPUT_SIZE];
        String hash = h0;
        for(int i = 0; i < numBlock; i++) {
            fis.read(currentBlock);
            if(!hash.equals(Cipher.toHexString(getHash(currentBlock)))) {
                fis.close();
                return false;
            }
            hash = Cipher.toHexString(Arrays.copyOfRange(currentBlock, BLOCK_SIZE, BLOCK_SIZE + OUTPUT_SIZE));
        }
        currentBlock = new byte[lastBlock];
        fis.read(currentBlock);
        fis.close();
        if(!hash.equals(Cipher.toHexString(getHash(currentBlock)))) {
            return false;
        }
        return true;
    }
    
    public static boolean verifyAndRecover(String sourcePath, String targetPath, String h0)
            throws IOException, NoSuchAlgorithmException {
        File f = new File(sourcePath);
        FileInputStream fis = new FileInputStream(f);
        FileOutputStream fos = new FileOutputStream(targetPath);
        long numBlock = f.length() / (BLOCK_SIZE + OUTPUT_SIZE);
        int lastBlock = (int) f.length() % (BLOCK_SIZE + OUTPUT_SIZE);
        byte[] currentBlock = new byte[BLOCK_SIZE + OUTPUT_SIZE];
        String hash = h0;
        for(int i = 0; i < numBlock; i++) {
            fis.read(currentBlock);
            if(!hash.equals(Cipher.toHexString(getHash(currentBlock)))) {
                fis.close();
                fos.close();
                return false;
            }
            hash = Cipher.toHexString(Arrays.copyOfRange(currentBlock, BLOCK_SIZE, BLOCK_SIZE + OUTPUT_SIZE));
            fos.write(currentBlock, 0, BLOCK_SIZE);
        }
        currentBlock = new byte[lastBlock];
        fis.read(currentBlock);
        fis.close();
        if(!hash.equals(Cipher.toHexString(getHash(currentBlock)))) {
            fos.close();
            return false;
        }
        fos.write(currentBlock);
        fos.close();
        return true;
    }
    
    private static byte[] getHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        return sha256.digest(data);
    }
    
    private static void writeFile(Node current, String targetPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(targetPath);
        while(current != null) {
            fos.write(current.block);
            current = current.next;
        }
        fos.close();
    }
    
    public static void main(String[] args) {
        try {
            String hash = signFile("data/TargetVideo.mp4", "data/TargetVideoHashed.txt");
            System.out.println(hash);
            System.out.println(verify("data/TargetVideoHashed.txt", "5b96aece304a1422224f"
                   + "9a41b228416028f9ba26b0d1058f400200f06a589949"));
            System.out.println(verifyAndRecover("data/TargetVideoHashed.txt", "data/TargetVideoRecovered.mp4",
                   "5b96aece304a1422224f9a41b228416028f9ba26b0d1058f400200f06a589949"));
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }
}
