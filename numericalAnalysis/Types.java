class FiniteDigitDecimal extends Number implements Comparable<FiniteDigitDecimal> {
  private int power;
  private int[] digits;
  private int sign;

  public FiniteDigitDecimal(double n, int precision) {
    // First lets determine the sign.
    if (n < 0) sign = -1;
    else sign = 1;
    // Make sure the float is positive for the setup.
    n *= sign;

    digits = new int[precision];
    setup(n);
  }
  public FiniteDigitDeciaml(int sign, int[] digits, int power) {
    this.sign = sign;
    this.digits = new int[digits.length];
    for (int i = 0; i < digits.length; i++)
      this.dihgits[i] = digits[i];
    this.power = power;
  }
  public FiniteDigitDecimal(FiniteDigitDecimal n) {
    this.sign = n.sign;
    this.power = n.power;
    this.digits = new int[n.digits.length];
    for (int i = 0; i < this.digits.length; i++)
      this.digits[i] = n.digits[i];
  }
  

  private void setup(double n) {
    /*
     * Setup to float such that there is only one digit before the decimal point.
     * And determine the power. 
     */ 
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

  private void round(int n) {
    n += 5;
    n /= 10;
    digits[digits.length - 1] += n;
  }

  private void shift() {
    for (int i = digits.length - 1; i > 0; i--) {
      if (digits[i] < 0) {
        digits[i-1] -= 1; 
        digits[i] += 10;
      } else {
        digits[i-1] += digits[i]/10;
        digits[i] = digits[i] % 10;
      }
    }
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

  public FiniteDigitDecimal add(FiniteDigitDecimal n) {
    if (this.digits.length != n.digits.length) 
      return null;
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

  public FiniteDigitDecimal multiply(FiniteDigitDecimal n) {
    FiniteDigitDecimal t = this.clone();
    if (t.digits.length != n.digits.length) 
      return null;
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
    setup(i0);
    // Determine the new power.
    t.power -= 2*digits.length;
    t.power += powSum;
    return t;
  }

  public FiniteDigitDecimal divide(FiniteDigitDecimal n) {
    return new FiniteDigitDecimal(
                                  this.doubleValue() / n.doubleValue(), 
                                  this.digits.length
                                 );
  }

  public FiniteDigitDecimal power(int n) {
    if (n <= 1) return null;
    FiniteDigitDecimal m = this.clone();
    while (n > 1) {
      m = m.multiply(this);
      n--;
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
    return n;
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
