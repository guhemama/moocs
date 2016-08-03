
case class Book(title: String, authors: List[String])

val books: Set[Book] = Set(
  Book(title = "Structure and Interpretation of Computer Programs",
      authors = List("Abelson, Harald", "Sussman, Gerald J.")),
  Book(title = "Introduction to Functional Programming",
      authors = List("Bird, Richard", "Wadler, Phil")),
  Book(title = "Effective Java",
      authors = List("Bloch, Joshua")),
  Book(title = "Java Puzzlers",
      authors = List("Bloch, Joshua", "Gafter, Neal")),
  Book(title = "Programming in Scala",
      authors = List("Odersky, Martin", "Spoon, Lex", "Venners, Bill")))


// Find a book which was written by 'Bird'
for (b <- books; a <- b.authors if a startsWith "Bird") yield b.title

// Version with high-order functions
books flatMap(b =>
  b.authors withFilter(a => a startsWith "Bird") map (y => b.title))

// Find authors who wrote more than a book
// Creates pairs of all the books
for {
  b1 <- books
  b2 <- books
  if b1.title < b2.title
  a1 <- b1.authors
  a2 <- b2.authors
  if a1 == a2
} yield a1

// Remove duplicates from a collection (not from a Set)
//{
//  for {
//    b1 <- books
//    b2 <- books
//    if b1.title < b2.title
//    a1 <- b1.authors
//    a2 <- b2.authors
//    if a1 == a2
//  } yield a1
//}.distinct

// Assign list result to val k
val k = for (b <- books; a <- b.authors if a startsWith "Bloch") yield b.title

