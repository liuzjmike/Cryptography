import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class CloseRSAPrimes {
    
    private static final BigDecimal TWO = new BigDecimal(2);

    public static String decrypt(BigInteger cipher, BigInteger p, BigInteger q, BigInteger e) {
        BigInteger phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger d = e.modInverse(phiN);
        BigInteger plain = cipher.modPow(d, p.multiply(q));
        byte[] plainHex = plain.toByteArray();
        return extractMessage(plainHex);
    }
    
    private static String extractMessage(byte[] plainHex) {
        if(plainHex[0] != 2) {
            throw new RuntimeException("Invalid plaintext");
        }
        for(int i = 1; i < plainHex.length; i++) {
            if(plainHex[i] == 0) {
                return new String(Arrays.copyOfRange(plainHex, i+1, plainHex.length));
            }
        }
        throw new RuntimeException("Invalid plaintext");
    }

    /**
     * Takes in four integers that comprise the inequality
     * |aq-bp| < (2^c)*(n^(1/4)) and returns the factorization of n
     * @param n The RSA public modulus, the product of two primes p and q
     * @param a
     * @param b
     * @param c
     * @return an int[] containing the two factors of n
     */
    public static List<BigDecimal> factor(BigDecimal n, int a, int b, int c) {
        MathContext mc = new MathContext(n.precision());
        BigDecimal coeff = new BigDecimal(a*b);
        double dist = Math.pow(2, 2*c-3);
        boolean toHalf = (a+b)%2 != 0;
        BigDecimal mid = round(sqrt(n.multiply(coeff), mc), toHalf, RoundingMode.CEILING);
        for(int i = 0; i < dist; i++) {
            
            //mid = sqrt(abn) + i
            //x = sqrt(mid^2 - abn)
            BigDecimal guess = mid.add(new BigDecimal(i));
            BigDecimal x = round(sqrt(guess.pow(2).subtract(coeff.multiply(n)), mc), toHalf, RoundingMode.HALF_UP);
            System.out.println(i);
            BigDecimal low = guess.subtract(x);
            BigDecimal high = guess.add(x);
            if(low.multiply(high).compareTo(n.multiply(coeff)) == 0) {
                if(a == 1 && b == 1) {
                    return Arrays.asList(low, high);
                } else {
                    return findPQ(n, low, high, a, b);
                }
            }
        }
        throw new RuntimeException("Factorization failed");
    }
    
    private static BigDecimal round(BigDecimal num, boolean toHalf, RoundingMode rm) {
        if(toHalf) {
            return num.multiply(TWO).setScale(0, rm).divide(TWO);
        } else {
            return num.setScale(0, rm);
        }
    }

    private static List<BigDecimal> findPQ(BigDecimal n, BigDecimal low, BigDecimal high, int a, int b) {
        BigDecimal bigA = new BigDecimal(a);
        BigDecimal bigB = new BigDecimal(b);
        if(low.remainder(bigA).compareTo(BigDecimal.ZERO) == 0
                && high.remainder(bigB).compareTo(BigDecimal.ZERO) == 0) {
            return Arrays.asList(low.divide(bigA), high.divide(bigB));
        } else {
            return Arrays.asList(low.divide(bigB), high.divide(bigA));
        }
    }

    private static BigDecimal sqrt(BigDecimal n, MathContext mc) {
        BigDecimal two = new BigDecimal(2);
        BigDecimal r = n.divide(two);
        BigDecimal s = two;
        BigDecimal bound = new BigDecimal("0.0000000001");
        while(r.subtract(s).abs().compareTo(bound) > 0) {
            r = r.add(s).divide(two, mc);
            s = n.divide(r, mc);
        }
        return r;
    }
    
    public static void main(String[] args) {
        BigDecimal n1 = new BigDecimal("1797693134862315907729305190789024733617976"
                + "9789423065727343008115773267580550562068698537944921298295958550"
                + "1387537164015710139858647833778606925583497541085196591615128057"
                + "5759407526350074759352887108236499499407718956170543611494748650"
                + "46711015101563940680527540071584560878577663743040086340742855278549092581");
        BigDecimal n2 = new BigDecimal("6484558428080716696628242653467722787263437"
                + "2070697626306043907037879730861808111646271401527606141756919558"
                + "7321840254520655424906719892428844841839353281972988531310511738"
                + "6489659625828215025049902644521008852816733037111422964210278402"
                + "89307657458645233683357077834689715838646088239640236866252211790085787877");
        BigDecimal n3 = new BigDecimal("7200622637473504252795644355255837383380844"
                + "5147399984182665305798191635569018833779042340866418766393848517"
                + "5264994017897083524079135686877441155132015188279331812309091996"
                + "2463618968365736431191740949613485246397078852387993968392303646"
                + "76670221627018353299443241192173812729276147530748597302192751375739387929");
        BigInteger cipher = new BigInteger("2209645186741038177630656113488341801741006978789"
                + "28310717318391436761356001205380042823296504735094243439462197515122564658"
                + "39967942889460764542040581564748988013734864120452325229320176487916666402"
                + "99750918872997169052608322206777160001932926087000957999372407745896777369"
                + "7817571267229951148662959627934791540");
        /*
        List<BigDecimal> factors1 = factor(n1, 1, 1, 1);
        for(BigDecimal n: factors1) {
            System.out.println(n);
        }
        System.out.println(decrypt(cipher, factors1.get(0).toBigInteger(), factors1.get(1).toBigInteger(), new BigInteger("65537")));
        */
        List<BigDecimal> factors2 = factor(n2, 1, 1, 11);
        for(BigDecimal n: factors2) {
            System.out.println(n);
        }
        /*
        List<BigDecimal> factors3 = factor(n3, 3, 2, 0);
        for(BigDecimal n: factors3) {
            System.out.println(n);
        }
        */
    }
}
