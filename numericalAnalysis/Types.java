class FiniteDigitDecimal {
  private int power;
  private int[] digits;

  FiniteDigitDecimal(float n, int precision) {
    digits = new int[precision];
    setup(n);
  }

  boolean setup(float n) {
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
      digits[i-1] += digits[i]/10;
      digits[i] = digits[i] % 10;
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

    if (n.power > this.power) {
      int[] temp0 = this.digits;
      int temp1 = this.power;
      this.digits = n.digits;
      n.digits = temp0;
      this.power = n.power;
      n.power = temp1;
    }

    int overlap = this.digits.length - (this.power - n.power);
    for (int i = 0; i < overlap; i++) {
      this.digits[this.digits.length - overlap + i] += n.digits[i];
    }
    shift();
    if (overlap < this.digits.length)
      round(this.digits[overlap]);
    return true; 
  }

  boolean multiply(FiniteDigitDecimal n) {
    if (this.digits.length != n.digits.length) 
      return false;
    int i0 = 0;
    int i1 = 0;
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


    setup(i0);

    this.power -= 2*digits.length;
    this.power += powSum;
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
    return out;
  }
}
