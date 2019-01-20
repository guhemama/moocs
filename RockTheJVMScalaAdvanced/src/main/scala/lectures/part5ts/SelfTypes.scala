package lectures.part5ts

object SelfTypes extends App {
  // Requiring a type to be mixed in

  trait Instrumentalist {
    def play(): Unit
  }

  /**
    * Self type:
    *
    * "self: Instrumentalist =>" forces whoever implements Singer
    * must also implement Instrumentalist.
    *
    * Singer requires Instrumentalist
    */
  trait Singer { self: Instrumentalist =>
    def sing(): Unit
  }

  // This won't work.
  // class Vocalist extends Singer

  // This works.
  class LeadSinger extends Singer with Instrumentalist {
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

  // This works.
  val jamesHetfield = new Singer with Instrumentalist {
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("Guitar solo")
  }

  // This also works.
  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }
}
