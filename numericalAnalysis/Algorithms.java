public class Algorithms {
  public static void main(String[] args) {
    float num = (float)3.14159;
    FiniteDigitDecimal n = new FiniteDigitDecimal(25, 5);
    FiniteDigitDecimal m = new FiniteDigitDecimal((float)0.00037, 5);
    n.multiply(m);
    System.out.println(n);
  }
}
