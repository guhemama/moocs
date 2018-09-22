package lectures.part1as

object Recap extends App {
  val pairs = for {
    num <- List(1,2,3) if num > 1
    char <- (List('a','b','c'))
  } yield (num -> char)

  println(pairs)
}
