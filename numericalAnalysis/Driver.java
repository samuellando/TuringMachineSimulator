import java.lang.reflect.Method;

public class Driver {
  public static FiniteDigitDecimal f(FiniteDigitDecimal x) {
    return x.power(3).substract(new FiniteDigitDecimal(1, 1000));
  }

  public static void main(String[] args) {
    FiniteDigitDecimal a = new FiniteDigitDecimal(0, 5);
    FiniteDigitDecimal b = new FiniteDigitDecimal(3, 5);
    FiniteDigitDecimal tol = new FiniteDigitDecimal(0.00001, 5);
    Method f = null;
    try {
      f = Driver.class.getMethod("f", a.getClass());
    } catch (Exception e) {
      System.err.println("Couldn't find method f");
    }
    for (int i = 1; i < 10; i++) {
      System.out.println(Basic.bisectionMethod(f, a, b, tol, i));
    }
  }
}
