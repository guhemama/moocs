/**
  * Created by gustavo on 22/04/16.
  */
object Lesson26 {
  def main(args: Array[String]) {
    val x = new RationalNumber(1, 3)
    println(x.denominator)

    val y = new RationalNumber(5, 7)
    println(x + y)

    val z = new RationalNumber(3, 2)
    println(x - y - z)

    println(-x)

    // Infix notation
    x less y
  }
}
