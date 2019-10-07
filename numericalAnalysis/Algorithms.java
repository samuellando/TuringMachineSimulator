import java.lang.reflect.Method;

class Basic {
  public static FiniteDigitDecimal bisectionMethod(Method f, FiniteDigitDecimal a, FiniteDigitDecimal b, FiniteDigitDecimal tol, int n) {
    final FiniteDigitDecimal f2 = new FiniteDigitDecimal(2, 1000);
    FiniteDigitDecimal pn = null;
    FiniteDigitDecimal pnmin1 = a.clone();
    for (int i = 0; i < n; i++) {
      pn = b.add(a).divide(f2);

      if (pn.substract(pnmin1).abs().compareTo(tol) < 0)
        return pn;
      try {
        if (((FiniteDigitDecimal)f.invoke(null, a)).sign()*((FiniteDigitDecimal)f.invoke(null, pn)).sign() < 0)
          b = pn;
        else
          a = pn;
      } catch (Exception e) {
        System.out.println("Problem with f");
        System.exit(-1);
      }
      pnmin1 = pn;
    }
    return pn;
  }
}
