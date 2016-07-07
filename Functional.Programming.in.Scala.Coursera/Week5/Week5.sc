object Week5 {
  def removeAt[T](n: Int, xs: List[T]): List[T] = (xs take n) ::: (xs drop n + 1)

  removeAt(1, List('a', 'b', 'c', 'd')) // List('a', 'c', 'd'))

  pack(List("a", "a", "a", "b", "c", "c", "a"))
  //List(List("a", "a", "a"), List("b"), List("c", "c"), List("a"))

  def pack[T](xs: List[T]): List[List[T]] = xs match {
    case Nil => Nil
    case x :: xs1 => {
      val groupedList   = xs takeWhile (k => k == x)
      val remainderList = xs dropWhile (y => y == x)
      groupedList :: pack(remainderList)
    }
  }

  /* Complete the following definitions of the basic functions map and length
   * on lists, such that their implementation uses foldRight:
   */
  def mapFun[T, U](xs: List[T], f: T => U): List[U] =
    (xs foldRight List[U]())( ??? )

  def lengthFun[T](xs: List[T]): Int =
    (xs foldRight 0)((x, y) => 1 + y)

  lengthFun(List(1,2,3))
}