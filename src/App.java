import java.math.BigDecimal;

public class App {
    public static void main(String[] args) throws Exception {
        long t = System.currentTimeMillis();
        System.out.println(BigMath.fact(BigDecimal.valueOf(800)));
        System.out.println(System.currentTimeMillis() - t);
    }
}
