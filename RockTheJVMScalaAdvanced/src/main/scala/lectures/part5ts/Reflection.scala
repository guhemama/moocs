package lectures.part5ts

object Reflection extends App {
  /**
    * Solves the problem of instantiating objects at runtime.
    * Meta-programming => reflection + macros + quasiquotes
    */
  case class Person(name: String) {
    def sayMyName(): Unit = println(s"My name is $name")
  }

  // Import the lib
  import scala.reflect.runtime.{universe => ru}

  // Setup a mirror
  val m = ru.runtimeMirror(getClass.getClassLoader)

  // Create a class object
  val klass = m.staticClass("lectures.part5ts.Reflection.Person")

  // Create a reflected mirror
  val classMirror = m.reflectClass(klass)

  // Get the reflected class constructor
  val constructor = klass.primaryConstructor.asMethod

  // Reflect the constructor
  val constructorMirror = classMirror.reflectConstructor(constructor)

  // Invoke the constructor
  val instance = constructorMirror.apply("Hans")

  println(instance)

  // Create an instance from data that comes from somewhere else
  val person = Person("Maria")
  val methodName = "sayMyName"

  // Reflect the instance
  val reflected = m.reflect(person)

  // Create a method symbol
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod

  // Reflect the method
  val reflectedMethod = reflected.reflectMethod(methodSymbol)

  // Invoke the method
  reflectedMethod.apply()
}
