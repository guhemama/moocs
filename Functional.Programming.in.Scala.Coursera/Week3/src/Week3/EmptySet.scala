package Week3

class EmptySet extends IntSet {
  def contains(x: Int): Boolean = false

  def incl(x: Int): IntSet = new NonEmptySet(x, new EmptySet, new EmptySet)

  def union(other: IntSet): IntSet = other

  override def toString = "."
}
