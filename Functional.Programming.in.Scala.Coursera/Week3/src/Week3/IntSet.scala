package Week3

/**
  * Created by gustavo on 26/04/16.
  */
abstract class IntSet {
  def incl(x: Int): IntSet
  def contains(x: Int): Boolean
  def union(other: IntSet): IntSet
}
