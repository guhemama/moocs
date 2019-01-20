package lectures.part4implicits

object TypeClasses extends App {
  trait HtmlWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HtmlWritable {
    override def toHtml: String = s"<div>$name ($age yo)</div>"
  }

  val john = User("John", 32, "john@examepl.com")
  john.toHtml

  /**
    * The impl above is not easily extensible.
    * The approach below is more type safe.
    */
  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  object UserSerializer extends HTMLSerializer[User] {
    def serialize(user: User): String = s"<div>${user.name} (${user.age} yo)</div>"
  }

  println(UserSerializer.serialize(john))

  // Type Class (usually includes a type parameter
  trait MyTypeClassTemplate[T] {
    def action(value: T): String
  }

  /**
    * Equality, an example of type class.
    */
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div>$value</div>"
  }

  implicit object UserSerializer2 extends HTMLSerializer[User] {
    def serialize(user: User): String = s"<div>${user.name} (${user.age} yo)</div>"
  }

  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(john))

  // Has access to entire class interface.
  println(HTMLSerializer[User].serialize(john))

  /**
    * Part 3
    */
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println(john.toHTML)

  /**
    * We can also extend this to new types...
    */
  println(2.toHTML)
  println(2.toHTML(IntSerializer)) // We can also be explicit

  /**
    * Implementing === and !==
    */
  implicit class TypeSafeEqual[T](value: T) {
    def ===(other: T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(value, other)
    def !==(other: T)(implicit equalizer: Equal[T]): Boolean = !equalizer.apply(value, other)
  }

  val anotherJohn = User("John", 32, "john@examepl.com")
  println(john === anotherJohn)

  implicit object StringEquality extends Equal[String] {
    override def apply(a: String, b: String): Boolean = a.equals(b)
  }

  println("John" === "bob")

  /**
    * Implicitly
    */
  case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("0755")

  // Extract the current value of an implicit val
  val standardPermissions = implicitly[Permissions]

  // In a method...
  def printStandardPermissions(): Unit = println(implicitly[Permissions])
  printStandardPermissions()
}
