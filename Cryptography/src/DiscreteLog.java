import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class DiscreteLog {
    
    //B = 2^20
    private static final BigInteger B = new BigInteger("1048576");
    private static final BigInteger ONE = new BigInteger("1");
    
    public static BigInteger dlog(BigInteger p, BigInteger g, BigInteger h) {
        Map<BigInteger, BigInteger> middlePool = getMiddle(p, g, h);
        BigInteger base = g.modPow(B, p);
        BigInteger x0 = new BigInteger("0");
        while(x0.compareTo(B) < 0) {
            System.out.println(x0);
            BigInteger rhs = base.modPow(x0, p);
            if(middlePool.containsKey(rhs)) {
                return B.multiply(x0).mod(p).add(middlePool.get(rhs).mod(p));
            }
            x0 = x0.add(ONE);
        }
        throw new RuntimeException("No result");
    }
    
    private static Map<BigInteger, BigInteger> getMiddle(BigInteger p, BigInteger g, BigInteger h) {
        Map<BigInteger, BigInteger> middlePool = new HashMap<BigInteger, BigInteger>();
        BigInteger x1 = new BigInteger("0");
        while(x1.compareTo(B) < 0) {
            System.out.println(x1);
            BigInteger denom = g.modPow(x1, p).modInverse(p);
            BigInteger lhs = h.multiply(denom).mod(p);
            if(!middlePool.containsKey(lhs)) {
                middlePool.put(lhs, x1);
            } else {
                break;
            }
            x1 = x1.add(ONE);
        }
        return middlePool;
    }

    public static void main(String[] args) {
        BigInteger p = new BigInteger("13407807929942597099574024998205846127479365820592393377723561"
                + "4437217640300735469768018742981669034276900318581864860508537538828119465699464336"
                + "49006084171");
        BigInteger g = new BigInteger("11717829880366207009516117596335367088558084999998952205599979"
                + "4590639294997365837466705721764714603129285948296754282794665665271152127484675898"
                + "94601965568");
        BigInteger h = new BigInteger("32394751040504504435652643787280657886490975209524495278347924"
                + "5297198197614329255807385693795855318053287892800149470609739410857758573245230767"
                + "3444020333");
        System.out.println(dlog(p, g, h));
    }

}
