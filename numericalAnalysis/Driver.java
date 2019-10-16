import java.lang.reflect.Method;
import java.lang.Math;

public class Driver {
  public static Double f(Double x) {
    return Double.valueOf(Math.pow(x,3) - 1);
  }

  public static void main(String[] args) {
    double a = 0;
    double b = 3;
    double tol = 0.000000001;
    double val = 0;
    Method f = null;
    try {
      f = Driver.class.getMethod("f", Double.valueOf(a).getClass());
    } catch (Exception e) {
      System.out.println("Couldn't find method f");
      System.exit(-1);
    }
    for (int i = 1; i < 100; i++) {
      val = Basic.bisectionMethod(f, a, b, tol, i);
      System.out.println(val+" "+f(val));
    }
  }
}
