public class Algorithms {
  public static void main(String[] args) {
    double pi = 3.14159;
    FiniteDigitDecimal fpi = new FiniteDigitDecimal(pi, 6);
    FiniteDigitDecimal f2 = new FiniteDigitDecimal(2, 7);
    FiniteDigitDecimal f120 = new FiniteDigitDecimal(120, 7);
    FiniteDigitDecimal f6 = new FiniteDigitDecimal(-6, 7);
    FiniteDigitDecimal sinpid2 = fpi.divide(f2).power(5).divide(f120).add(fpi.divide(f2).power(3).divide(f6)).add(fpi.divide(f2));
    System.out.println(sinpid2);
  }
}
