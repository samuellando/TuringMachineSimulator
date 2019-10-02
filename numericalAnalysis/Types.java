class FiniteDigitDecimal {
  private int power;
  private int[] digits;
  private int sign;

  FiniteDigitDecimal(float n, int precision) {
    // First lets determine the sign.
    if (n < 0) sign = -1;
    else sign = 1;
    // Make sure the float is positive for the setup.
    n *= sign;

    digits = new int[precision];
    setup(n);
  }
  FiniteDigitDecimal(FiniteDigitDecimal n) {
    this.sign = n.sign;
    this.power = n.power;
    this.digits = new int[n.digits.length];
    for (int i = 0; i < this.digits.length; i++)
      this.digits[i] = n.digits[i];
  }

  boolean setup(float n) {
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
    return true;
  }

  void round(int n) {
    n += 5;
    n /= 10;
    digits[digits.length - 1] += n;
  }

  void shift() {
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

  boolean add(FiniteDigitDecimal n) {
    if (this.digits.length != n.digits.length) 
      return false;
    boolean substraction = false;
    if (this.sign == -1 ^ n.sign == -1) 
      substraction = true;
    // Make the number with the highest power the dominant one.
    if (n.power > this.power) {
      int[] temp0 = this.digits;
      int temp1 = this.power;
      int temp2 = this.sign;
      this.digits = n.digits;
      n.digits = temp0;
      this.power = n.power;
      n.power = temp1;
      this.sign = n.sign;
      n.sign = temp2;
    }
    // Add the overlapping elements.
    int overlap = this.digits.length - (this.power - n.power);
    for (int i = 0; i < overlap; i++) {
      if (substraction)
        this.digits[this.digits.length - overlap + i] -= n.digits[i];
      else
        this.digits[this.digits.length - overlap + i] += n.digits[i];
    }
    // Preform a shift.
    shift();
    // If there is an extra element, round.
    if (overlap < this.digits.length)
      round(this.digits[overlap]);
    return true; 
  }

  boolean multiply(FiniteDigitDecimal n) {
    if (this.digits.length != n.digits.length) 
      return false;
    if (this.sign == -1 ^ n.sign == -1) 
      this.sign = -1;
    else
      this.sign = 1;
    // Convert the operands to integers.
    long i0 = 0;
    long i1 = 0;
    // Determine the new power. 
    int powSum = this.power + n.power;
    for (int i = 0; i < this.digits.length; i++) {
      i0 += this.digits[i];
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
    this.power -= 2*digits.length;
    this.power += powSum;
    return true;
  }

  boolean power(int n) {
    if (n <= 1) return false;
    FiniteDigitDecimal m = new FiniteDigitDecimal(this);
    while (n > 1) {
      this.multiply(m);
      n--;
    } 
    return true;
  }

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
}
