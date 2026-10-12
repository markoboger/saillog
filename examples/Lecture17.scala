//> using scala 3.9.0
//> using dep com.lihaoyi::upickle:4.4.3
// Lecture 17 (Domain-Driven Design): given, using and derives – the examples from the slides.
// Run: scala-cli run examples/Lecture17.scala
import scala.language.strictEquality
import upickle.default.*

// ---- given: providing a type-class instance ----
trait Show[A]:
  def show(a: A): String

case class Isbn(value: String) derives CanEqual, ReadWriter

enum Genre derives CanEqual, ReadWriter:
  case Novel, Science, Children

case class Book(title: String, isbn: Isbn, genre: Genre)
    derives CanEqual, ReadWriter

given Show[Book] with
  def show(b: Book) = s"${b.title} (${b.genre})"

given Show[Genre] with
  def show(g: Genre) = g.toString.toLowerCase

// ---- using: receiving the instance implicitly ----
def printAll[A](xs: List[A])(using s: Show[A]): Unit =
  xs.foreach(x => println(s.show(x)))

def describe[A: Show](a: A): String = summon[Show[A]].show(a)

// ---- what derives replaces: a hand-written given ----
enum Format:
  case Paper, Ebook
object Format:
  given CanEqual[Format, Format] = CanEqual.derived

@main def lecture17(): Unit =
  val ddd = Book("Domain-Driven Design", Isbn("9780321125217"), Genre.Science)
  val momo = Book("Momo", Isbn("9783522202107"), Genre.Children)
  printAll(List(ddd, momo))
  printAll(List(Genre.Novel, Genre.Science))
  println(describe(ddd))
  println(Genre.Novel == Genre.Science)
  println(Isbn("9780321125217") == Isbn("9780321125217"))
  println(Format.Paper == Format.Ebook)
  println(write(ddd))
  println(read[Book](write(ddd)) == ddd)
