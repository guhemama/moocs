package lectures.part4implicits

import java.util.Date

object JSONSerialization extends App {
  /**
    * Serialize Users, Posts and Feeds.
    */
  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /**
    * 1. Intermediate data types: Int, String, List, Date, etc.
    * 2. Type classes for conversion to intermediate data types.
    * 3. Serialize these data types to JSON.
    */
  // An intermediate data type
  sealed trait JSONValue {
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    def stringify: String = "\"" + value + "\""
  }
  final case class JSONNumber(value: Int) extends JSONValue {
    def stringify: String = value.toString
  }
  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    def stringify: String = values.map(_.stringify).mkString("[", ",", "]")
  }
  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    def stringify: String = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }.mkString("{", ",", "}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Daniel"),
    "posts" -> JSONArray(List(
      JSONString("Scala Rocks"),
      JSONNumber(42)
    ))
  ))

  println(data.stringify)

  // Create our type class, and their implicits instances.
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  implicit object StringConverter extends JSONConverter[String] {
    def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object NumberConverter extends JSONConverter[Int] {
    def convert(value: Int): JSONValue = JSONNumber(value)
  }

  implicit object UserConverter extends JSONConverter[User] {
    def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }

  implicit object PostConverter extends JSONConverter[Post] {
    def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(post.content),
      "created" -> JSONString(post.createdAt.toString)
    ))
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    def convert(feed: Feed): JSONValue = JSONObject(Map(
      "user"  -> feed.user.toJSON,
      "posts" -> JSONArray(feed.posts.map(_.toJSON))
    ))
  }

  // Conversion
  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue =
      converter.convert(value)
  }

  // Call stringify on result
  val now = new Date(System.currentTimeMillis())
  val john = User("John", 42, "john@example.com")
  val feed = Feed(john, List(
    Post("hello world", now),
    Post("good bye", now)
  ))

  println(feed.toJSON)
  println(feed.toJSON.stringify)
}