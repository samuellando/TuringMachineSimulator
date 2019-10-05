import java.lang.reflect.Method;

class Basic {
  public static FiniteDigitDecimal bisectionMethod(Method f, FiniteDigitDecimal a, FiniteDigitDecimal b, FiniteDigitDecimal tol, int n) {
    final FiniteDigitDecimal f2 = new FiniteDigitDecimal(2, 1000);
    FiniteDigitDecimal pn;
    FiniteDigitDecimal pnmin1 = a.clone();
    for (int i = 0; i < n; i++) {
      pn = a.add(b).divide(f2);

      if (pn.substract(pnmin1).abs().compareTo(tol) < 0)
        return pn;

      if (a.sign()*pn.sign() < 0)
        b = pn;
      else
        a = pn;
      pnmin1 = pn;
    }
    return null;
  }
}
