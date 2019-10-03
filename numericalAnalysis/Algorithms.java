public class Algorithms {
  public static void main(String[] args) {
    float num = (float)3.14159;
    FiniteDigitDecimal n = new FiniteDigitDecimal(25, 5);
    FiniteDigitDecimal m = new FiniteDigitDecimal(5, 4);
    System.out.println(n.divide(m));
  }
}
