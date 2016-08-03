/**
  * A new type for rational numbers.
  */
class RationalNumber(numer: Int, denom: Int) {
  // Precondition: predefined function that takes a condition and an optional message
  require(denom != 0, "Denominator can't be zero.")

  private val g = gcd(numer, denom)

  val numerator   = numer / g
  val denominator = denom / g

  // A second constructor for the class, takes a single arg
  def this(x: Int) = this(x, 1)

  def add(that: RationalNumber): RationalNumber =
    new RationalNumber(
      numerator * that.denominator + that.numerator * denominator,
      denominator * that.denominator
    )

  def +(that: RationalNumber): RationalNumber =
    new RationalNumber(
      numerator * that.denominator + that.numerator * denominator,
      denominator * that.denominator
    )

  // Override because this has already been implemented
  override def toString = numerator + "/" + denominator

  def neg = new RationalNumber(-numerator, denominator)

  // Operation on a unit
  def unary_- = new RationalNumber(-numerator, denominator)

  def sub(that: RationalNumber): RationalNumber = add(that.neg)
  def -(that: RationalNumber): RationalNumber = add(that.neg)

  private def gcd(a: Int, b: Int): Int =  if (b == 0) a else gcd(b, a % b)

  def less(that: RationalNumber): Boolean = numerator * that.denominator < that.numerator * denominator

  // Another notation
  def <(that: RationalNumber): Boolean = numerator * that.denominator < that.numerator * denominator

  def max(that: RationalNumber): RationalNumber = if (this.less(that)) that else this
}
