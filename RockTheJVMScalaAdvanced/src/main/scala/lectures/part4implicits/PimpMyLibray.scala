package lectures.part4implicits

object PimpMyLibrary extends App {
  // This is an implicit class that wraps an Int.
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0
    def isOdd: Boolean = !isEven
    def sqrt: Double = Math.sqrt(value)
  }

  // This is called type enrichment.
  42.isEven

  // E.g. fromm the std library
  import scala.concurrent.duration._
  5.seconds

  /**
    * Exercise: enrich the String class
    */
  implicit class RichString(val value: String) extends AnyVal {
    def asInt: Int = value.length
    def encrypt: String = value.map(c => (c + 1).toChar)
  }

  println("John Doe".encrypt)

  // Implicit conversions
  implicit def stringToInt(string: String): Int = Integer.valueOf(string)
  println("6" / 2) // Divisions à la JavaScript
}
