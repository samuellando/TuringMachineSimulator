/**
 * This class implements a decimal number with a fixed number of significant
 * figures.
 *
 * @author Samuel Lando
 * @since 2019-10-04
 */
class FiniteDigitDecimal extends Number implements Comparable<FiniteDigitDecimal> {
  private int power;
  private int[] digits;
  private int sign;
  /**
   * Convert double to a FiniteDigitDecimal with specify precission.
   * @param n The value to load.
   * @param precision The number of significant figures to keep.
   */
  public FiniteDigitDecimal(double n, int precision) {
    // First lets determine the sign.
    if (n < 0) sign = -1;
    else sign = 1;
    // Make sure the float is positive for the setup.
    n *= sign;

    digits = new int[precision];
    load(n);
  }
  /**
   * The fully parameteraized constructor.
   * @param sign The parity of the dicimal.
   * @param digits The digits of the decimal.
   * @param power The power of the decimal in leading point form.
   */
  public FiniteDigitDecimal(int sign, int[] digits, int power) {
    this.sign = sign;
    this.digits = new int[digits.length];
    for (int i = 0; i < digits.length; i++)
      this.digits[i] = digits[i];
    this.power = power;
  }
  /**
   * The copy constructor.
   * @param n The value to copy.
   */
  public FiniteDigitDecimal(FiniteDigitDecimal n) {
    this.sign = n.sign;
    this.power = n.power;
    this.digits = new int[n.digits.length];
    for (int i = 0; i < this.digits.length; i++)
      this.digits[i] = n.digits[i];
  }
  
  /**
   * Load a double into the digits array.
   * @param n The double that needs to be loaded
   */
  private void load(double n) {
    // Set up the float in scientific notation form.
    power = 0;
    while (n > 1) {
      n /= 10;
      power++;
    }
    while (n < 1) {
      n *= 10;
      power--;
    }
    power++;

    // Extract the digits form the float.
    for (int i = 0; i < digits.length; i++) {
      digits[i] = (int)n % 10;
      n *= 10;
    }
    round((int)n % 10);
  }
  /**
   * Given the first digit beyond the precision of the decimal, round.
   * @param n The first digit beyond the precision of the decimal.
   */
  private void round(int n) {
    // If n + 5 > 10, we will round up.
    n += 5;
    n /= 10;
    digits[digits.length - 1] += n;
    shift();
  }
  /**
   * Tranforms the decimal array such that every digit is less than 10.
   */
  private void shift() {
    // Do all the digits except for the first.
    for (int i = digits.length - 1; i > 0; i--) {
      if (digits[i] < 0) {
        digits[i-1] -= 1; 
        digits[i] += 10;
      } else {
        digits[i-1] += digits[i]/10;
        digits[i] = digits[i] % 10;
      }
    }
    // If the first needs to be shifted, we need to insert a 1 at the beggining.
    if (digits[0] >= 10) {
      power++;
      int over = digits[digits.length - 1];
      for (int i = 0; i < digits.length-1; i++)
        digits[i+1] = digits[i];
      digits[0] = digits[1]/10;
      digits[1] = digits[1]%10;
      round(over);
    }
  }
  /**
   * Update the precision of the decimal so it carries more or less sigfigs.
   * @param n The new precision.
   */
  private void updatePrecision(int n) {
    int[] newDigits = new int[n];
    for (int i = 0; i < n; i++)
      newDigits[i] = digits[i];
    int[] oldDigits = this.digits;
    this.digits = newDigits;
    if (oldDigits.length > n)
      round(oldDigits[n]);
  }
  /**
   * Accessor for the sign of the decimal.
   * @return -1 or 1.
   */
  public int sign() {
    return sign;
  }
  /**
   * Take the sum of this and another decimal.
   * @param n The other decimal.
   * @return The sum.
   */
  public FiniteDigitDecimal add(FiniteDigitDecimal n) {
    boolean substraction = false;
    if (this.sign == -1 ^ n.sign == -1) 
      substraction = true;
    // Make the number with the highest power the dominant one.
    FiniteDigitDecimal t = this.clone();
    if (n.power > t.power) {
      int[] temp0 = t.digits;
      int temp1 = t.power;
      int temp2 = t.sign;
      t.digits = n.digits;
      n.digits = temp0;
      t.power = n.power;
      n.power = temp1;
      t.sign = n.sign;
      n.sign = temp2;
    }
    if (t.digits.length > n.digits.length) 
      t.updatePrecision(n.digits.length);
    // Add the overlapping elements.
    int overlap = t.digits.length - (t.power - n.power);
    for (int i = 0; i < overlap; i++) {
      if (substraction)
        t.digits[t.digits.length - overlap + i] -= n.digits[i];
      else
        t.digits[t.digits.length - overlap + i] += n.digits[i];
    }
    // Preform a shift.
    t.shift();
    // If there is an extra element, round.
    if (overlap < t.digits.length)
      round(t.digits[overlap]);
    return t; 
  }
  /**
   * Take the product of this and another decimal.
   * @param n The other decimal.
   * @return The product.
   */
  public FiniteDigitDecimal multiply(FiniteDigitDecimal n) {
    FiniteDigitDecimal t = this.clone();

    if (t.digits.length > n.digits.length) 
      t.updatePrecision(n.digits.length);

    if (t.sign == -1 ^ n.sign == -1) 
      t.sign = -1;
    else
      t.sign = 1;
    // Convert the operands to integers.
    long i0 = 0;
    long i1 = 0;
    // Determine the new power. 
    int powSum = t.power + n.power;
    for (int i = 0; i < t.digits.length; i++) {
      i0 += t.digits[i];
      i1 += n.digits[i];
      i0 *= 10;
      i1 *= 10;
    }
    i0 /= 10;
    i1 /= 10;
    i0 *= i1;

    // Load the product of integers into digits.
    t.load(i0);
    // Determine the new power.
    t.power -= 2*digits.length;
    t.power += powSum;
    return t;
  }
  /**
   * Take the quotient of this and another decimal.
   * @param n The other decimal.
   * @return The quotient.
   */
  public FiniteDigitDecimal divide(FiniteDigitDecimal n) {
    FiniteDigitDecimal t = this.clone();
    if (t.digits.length > n.digits.length) 
      t.updatePrecision(n.digits.length);
    return new FiniteDigitDecimal(
                                  t.doubleValue() / n.doubleValue(), 
                                  t.digits.length
                                 );
  }
  /**
   * Take the power of this decimal.
   * @param n The exponent.
   * @return x^n.
   */
  public FiniteDigitDecimal power(int n) {
    FiniteDigitDecimal m = this.clone();
    while (n != 1) {
      if (n > 1) {
        m = m.multiply(this);
        n--;
      } else {
        m = m.divide(this);
        n++;
      }
    } 
    return m;
  }

  // java.lang.Comparable overrides.
  @Override
  public int compareTo(FiniteDigitDecimal n) {
    if (this.sign != n.sign) return this.sign;
    if (this.power != n.power) return this.power - n.power;

    int c;
    if (this.digits.length < n.digits.length)
      c = this.digits.length;
    else
      c = n.digits.length;

    for (int i = 0; i < c; i++)
      if (this.digits[i] != n.digits[i]) 
        return this.digits[i] - n.digits[i];
    return 0;
  }

  // java.lang.Number overrides.
  @Override
  public double doubleValue() {
    double n = 0;
    for (int i = this.digits.length - 1; i >= 0; i--) {
      n += 0.1 * this.digits[i];
      n /= 10;
    }
    for (int i = power; i != 0;)
      if (i > 0) {
        n *= 10;
        i--;
      } else {
        n /= 10;
        i++;
      }
    return sign*n;
  }
  @Override
  public float floatValue() {
    return (float)doubleValue();
  }
  @Override
  public int intValue() {
    return (int)doubleValue();
  }
  @Override
  public long longValue() {
    return (long)doubleValue();
  }
  
  // java.Object overrides.
  @Override
  public String toString() {
    String out = "";
    for (int i = 0; i < digits.length; i++)
      out = out+digits[i];
    if (power > 0) 
      out = out.substring(0, power)+"."+out.substring(power);
    else {
      for (int i = power; i < 0; i++)
        out = "0"+out;
      out = "0."+out;
    }
    return ((sign == -1)?"-":"")+out;
  }
  @Override
  public FiniteDigitDecimal clone() {
    return new FiniteDigitDecimal(this);
  }
  @Override
  public boolean equals(Object n) {
    if (!(n instanceof FiniteDigitDecimal)) return false;
    FiniteDigitDecimal nFDD = (FiniteDigitDecimal)n;
    return compareTo(nFDD) == 0;
  }
}
