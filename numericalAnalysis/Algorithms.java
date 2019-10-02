public class Algorithms {
  public static void main(String[] args) {
    float num = (float)3.14159;
    FiniteDigitDecimal n = new FiniteDigitDecimal(-7, 5);
    FiniteDigitDecimal m = new FiniteDigitDecimal(25, 5);
    n.add(m);
    System.out.println(n);
  }
}
