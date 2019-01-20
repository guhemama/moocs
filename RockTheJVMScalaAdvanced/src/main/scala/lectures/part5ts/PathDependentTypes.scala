package lectures.part5ts

object PathDependentTypes extends App {
  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i: Inner) = println(i)
    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    42
  }

  // The inner objects exist on only a per-instance basis.
  val outer = new Outer
  val inner = new outer.Inner // outer.Inner is a type

  val anotherOuter = new Outer
  // anotherOuter.print(inner) // This won't work.
  anotherOuter.printGeneral(inner) // But this works.

  /**
    * A practical example.
    */
  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }

  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42)
  get[StringItem]("home")
  // get[IntItem]("fail") // This doesn't work and is enforced by the compiler.
}
