import java.lang.reflect.Method;

public class Driver {
  public static FiniteDigitDecimal f(FiniteDigitDecimal x) {
    return x.power(3).substract(new FiniteDigitDecimal(1, 1000));
  }

  public static void main(String[] args) {
    FiniteDigitDecimal a = new FiniteDigitDecimal(-1, 5);
    FiniteDigitDecimal b = new FiniteDigitDecimal(3, 5);
    FiniteDigitDecimal tol = new FiniteDigitDecimal(0.00001, 5);
    FiniteDigitDecimal val;
    Method f = null;
    try {
      f = Driver.class.getMethod("f", a.getClass());
    } catch (Exception e) {
      System.out.println("Couldn't find method f");
      System.exit(-1);
    }
    for (int i = 1; i < 20; i++) {
      val = Basic.bisectionMethod(f, a, b, tol, i);
      System.out.println(val+" "+f(val));
    }
  }
}
