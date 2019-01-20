package lectures.part5ts

object RockingInheritance extends App {
  /**
    * Convenience
    */
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    def foreach(f: T => Unit): Unit
  }

  // We can use all the traits we need, defining a specific type.
  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  /**
    * Diamond problem
    */
  trait Animal {
    def name: String
  }
  trait Lion extends Animal {
    override def name: String = "Lion"
  }
  trait Tiger extends Animal {
    override def name: String = "Tiger"
  }

  // The last override (tiger) is the one that gets picked.
  class Liger extends Lion with Tiger
  val liger = new Liger
  println(liger.name)

  /**
    * The `super` problem (type linearization).
    */
  trait Cold {
    def print: Unit = println("Cold")
  }

  trait Green extends Cold {
    override def print: Unit = {
      println("Green")
      super.print
    }
  }

  trait Blue extends Cold {
    override def print: Unit = {
      println("Blue")
      super.print
    }
  }

  class Red {
    def print: Unit = println("Red")
  }

  /**
    * Type linearization:
    *
    * White = Red with Green with Blue with <White>
    *   = AnyRef with <Red>
    *     with (AnyRef with <Cold> with <Green>)
    *     with (AnyRef with <Cold> with <Blue>)
    *     with <White>
    *   = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>
    *
    * Calling `super` will call the types from right to left.
    */
  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("White")
      super.print
    }
  }

  val color = new White
  color.print
}
