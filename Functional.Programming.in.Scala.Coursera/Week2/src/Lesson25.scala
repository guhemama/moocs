/**
  * Created by gustavo on 22/04/16.
  */
object Lesson25 {
  def main(args: Array[String]) {
    val x = new RationalNumber(1, 3)
    println(x.denominator)

    val y = new RationalNumber(5, 7)
    println(x.add(y))

    val z = new RationalNumber(3, 2)
    println(x.sub(y).sub(z))

    println(x.less(y))

    // Infix notation
    x less y
  }
}
