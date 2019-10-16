import java.lang.reflect.Method;
import java.lang.Math;

class Basic {
  public static double bisectionMethod(Method f, double a, double b, double tol, int n) {
    double pn = 0;
    double pnmin1 = 0;
    for (int i = 0; i < n; i++) {
      pn = (a + b)/2;

      if (Math.abs(pn-pnmin1) < tol)
        return pn;
      try {
        if (((Double)f.invoke(null, a)).doubleValue()*((Double)f.invoke(null, pn)).doubleValue() < 0)
          b = pn;
        else
          a = pn;
      } catch (Exception e) {
        System.out.println(e);
        System.exit(-1);
      }
      pnmin1 = pn;
    }
    return pn;
  }
}
