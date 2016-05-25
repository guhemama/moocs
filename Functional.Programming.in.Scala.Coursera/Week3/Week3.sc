/**
  * Implementation of integer sets as a binary tree
  */
import Week3._

object Week3Worksheet {
  val t1 = new NonEmptySet(3, new EmptySet, new EmptySet)
  var t2 = t1 incl 4
  t2 incl 1

  t1 union t2

  // throw an exception
  def error(msg: String) = throw new Error(msg)
  //error("foobar")
}