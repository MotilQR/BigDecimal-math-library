import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class BigMath {
    public static final BigDecimal E = new BigDecimal("2.7182818284590452353602874713526624977572470936999595749669");
    public static final BigDecimal TWO_PI = new BigDecimal("6.283185307179586476925286766559");
    public static final BigDecimal PI = new BigDecimal("3.141592653589793238462643383279");
    public static final MathContext mc = new MathContext(120);
    private static final BigDecimal TWO = new BigDecimal(2);
    private static final BigDecimal LN2 = new BigDecimal("0.693147180559945309417232121458");

    public static BigDecimal exp(BigDecimal x) {
        if (x.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ONE;
        int n = x.setScale(0, RoundingMode.FLOOR).intValue();
        BigDecimal r = x.subtract(new BigDecimal(n), mc);
        BigDecimal intPart = powSys(E, n);
        BigDecimal term = BigDecimal.ONE;
        BigDecimal sum = BigDecimal.ONE;
        for (int i = 1; i < mc.getPrecision() * 2; i++) {
            term = term.multiply(r, mc).divide(new BigDecimal(i), mc);
            sum = sum.add(term, mc);
            if (term.compareTo(BigDecimal.ZERO) == 0) break;
        }

        return intPart.multiply(sum, mc);
    }

    public static BigDecimal log(BigDecimal x) {
        if (x.compareTo(BigDecimal.ZERO) <= 0) 
            throw new IllegalArgumentException("Logarithm is undefined for non-positive numbers.");
        
        int k = 0;
        while (x.compareTo(TWO) >= 0) {
            x = x.divide(TWO, mc);
            k++;
        }
        while (x.compareTo(BigDecimal.ONE) < 0) {
            x = x.multiply(TWO, mc);
            k--;
        }
        BigDecimal y = x.subtract(BigDecimal.ONE).divide(x.add(BigDecimal.ONE), mc);
        BigDecimal y2 = y.multiply(y, mc);
        BigDecimal term = y;
        BigDecimal sum = term;
        for (int i = 3; i < 100; i += 2) {
            term = term.multiply(y2, mc);
            sum = sum.add(term.divide(new BigDecimal(i), mc));
            if (term.compareTo(BigDecimal.ZERO) == 0) break;
        }
        sum = sum.multiply(TWO, mc);

        return sum.add(new BigDecimal(k).multiply(LN2, mc), mc);
    }

    private static BigDecimal powSys(BigDecimal base, int exponent) {
        if (exponent == 0) return BigDecimal.ONE;
        if (exponent < 0) return BigDecimal.ONE.divide(powSys(base, -exponent), mc);
        BigDecimal result = BigDecimal.ONE;
        BigDecimal power = base;
        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result = result.multiply(power, mc);
            }
            power = power.multiply(power, mc);
            exponent >>= 1;
        }
        return result;
    }

    public static BigDecimal log(BigDecimal x, BigDecimal base) {
        if (x.compareTo(BigDecimal.ZERO) <= 0) 
            throw new NumberFormatException();
        return log(x).divide(log(base), mc);
    }

    public static BigDecimal pow(BigDecimal x, BigDecimal power) {
        if (power.compareTo(BigDecimal.ZERO) == 0) 
            return new BigDecimal(1);
        if (x.compareTo(BigDecimal.ZERO) == -1) {
            if (power.remainder(BigDecimal.valueOf(2)).equals(BigDecimal.ZERO))
                return pow(x.negate(), power);
            else
                return pow(x.negate(), power).negate();
        }
        return exp(log(x).multiply(power, mc));
    }

    public static BigDecimal root(BigDecimal x, BigDecimal base) {
        if (x.compareTo(BigDecimal.ZERO) == 0) 
            throw new NumberFormatException();
        return exp(log(x).divide(base, mc));
    }

    public static BigDecimal cos(BigDecimal x) {
        BigDecimal s = x.multiply(BigDecimal.valueOf(2), mc).divide(PI, mc).remainder(BigDecimal.valueOf(2));
        if (s.equals(BigDecimal.valueOf(1.0)))
            return BigDecimal.valueOf(0);
        Double xa = reduceAngle(x);
        return BigDecimal.valueOf(Math.cos(xa));
    }
    public static BigDecimal sin(BigDecimal x) {
        String s = x.remainder(PI).toPlainString();
        if (new BigDecimal(s).equals(BigDecimal.ZERO))
            return BigDecimal.ZERO;
        Double xa = reduceAngle(x);
        return BigDecimal.valueOf(Math.sin(xa));
    }
    public static BigDecimal tg(BigDecimal x) {
        BigDecimal s = sin(x);
        BigDecimal c = cos(x);
        if (c.equals(BigDecimal.ZERO))
            throw new NumberFormatException();
        return s.divide(c, mc);
    }
    public static BigDecimal ctg(BigDecimal x) {
        BigDecimal s = sin(x);
        BigDecimal c = cos(x);
        if (s.compareTo(BigDecimal.valueOf(0.0)) == 0)
            throw new NumberFormatException();
        return c.divide(s, mc);
    }
    public static BigDecimal acos(BigDecimal x) {
        if (x.abs().compareTo(BigDecimal.ONE) == 1)
            throw new NumberFormatException();
        return BigDecimal.valueOf(Math.acos(x.doubleValue()));
    }
    public static BigDecimal asin(BigDecimal x) {
        if (x.abs().compareTo(BigDecimal.ONE) == 1)
            throw new NumberFormatException();
        return BigDecimal.valueOf(Math.asin(x.doubleValue()));
    }
    public static Double reduceAngle(BigDecimal x) {
        BigDecimal TwoPI = PI.multiply(BigDecimal.valueOf(2), mc);
        BigDecimal test = x.divideToIntegralValue(TwoPI);
        x = x.subtract(test.multiply(TwoPI, mc));
        return x.doubleValue();
    }

    public static BigDecimal fact(BigDecimal x) {
        if (x.compareTo(BigDecimal.ZERO) >= 0) {
            if (x == BigDecimal.valueOf(1) || x.compareTo(BigDecimal.ZERO) == 0) 
                return new BigDecimal(1);
            else 
                return fact(x.subtract(new BigDecimal(1), mc)).multiply(x, mc);
        } else 
            return fact(x.abs()).negate();
    }
    public static BigDecimal factFactor(int n) {
        if (n < 0)
            return factFactor(n * -1).negate();
        if (n == 0)
            return BigDecimal.ONE;
        if (n == 1 || n == 2)
            return BigDecimal.valueOf(n);

        boolean[] u = new boolean[n + 1];
        List<int[]> p = new ArrayList<>();

        for (int i = 2; i <= n; ++i) {
            if (!u[i]) {
                int k = n / i;
                int c = 0;
                while (k > 0) {
                    c += k;
                    k /= i;
                }
                p.add(new int[]{i, c});
                int j = 2;
                while (i * j <= n) {
                    u[i * j] = true;
                    ++j;
                }
            }
        }
        BigDecimal result = BigDecimal.ONE;
        for (int i = p.size() - 1; i >= 0; --i) {
            int base = p.get(i)[0];
            int exponent = p.get(i)[1];
            result = result.multiply(BigDecimal.valueOf(base).pow(exponent));
        }

        return result;
    }
}
