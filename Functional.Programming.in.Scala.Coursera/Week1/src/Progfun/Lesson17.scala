package Progfun

import scala.annotation._

/**
  * Created by gustavo on 16/04/16.
  */
object Lesson17 {
  def main(args: Array[String]) {
    println(gcd(100, 90))
    println(tailRecFactorial(4))
  }

  @tailrec
  def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)

  def factorial(n: Int): Int =
    if (n == 0) 1 else n * factorial(n - 1)

  def tailRecFactorial(n: Int): Int = {
    def loop(acc: Int, n: Int): Int =
      if (n == 0) acc
      else loop(acc * n, n - 1)
    loop(1, n)
  }
}
