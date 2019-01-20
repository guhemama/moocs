package lectures.part5ts

object StructuralTypes extends App {
  /**
    * Structural types.
    */
  type JavaCloseable = java.io.Closeable

  class HipsterCloseable {
    def close(): Unit = println("Closed!")
  }

  // We want to write a method that accept both types of Closeable
  type UnifiedCloseable = {
    def close(): Unit // This is a structural type. Any type that has a close method.
  }

  def closeQuietly(closeable: UnifiedCloseable): Unit = closeable.close()

  // Which allows us to do this:
  closeQuietly(new JavaCloseable {
    override def close(): Unit = println("Oy!")
  })

  closeQuietly(new HipsterCloseable)

  /**
    * Type refinements.
    */
  type AdvancedCloseable = JavaCloseable {
    def closeSilenty(): Unit
  } // AdvancedCloseable is a JavaCloseable with the closeSilenty method.

  class AdvancedJavaCloseable extends JavaCloseable {
    override def close(): Unit = println("Java closes!")
    def closeSilenty(): Unit = println("Java closes silently!")
  }

  def closeVerySilently(closeable: AdvancedJavaCloseable): Unit = closeable.closeSilenty()

  closeVerySilently(new AdvancedJavaCloseable)

  /**
    * Using structural types as standalone types.
    */
  // This closeable is its own type.
  def altClose(closeable: { def close(): Unit }): Unit = closeable.close()

  /**
    * Type-checking: duck typing. The compiler tests the types
    * at compile time. This can cause performance issues.
    */
  type SoundMaker = {
    def makeSound(): Unit
  }

  class Duck {
    def makeSound(): Unit = println("quack!")
  }

  class Car {
    def makeSound(): Unit = println("vrooom!")
  }

  val duck: SoundMaker = new Duck
  val car: SoundMaker = new Car
}
