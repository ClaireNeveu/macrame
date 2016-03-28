package macrame

import org.scalatest.FunSuite

import scala.math.Ordering

class DelegateTest extends FunSuite {

   test("Delegating a top-level object should fail.") {
      assertTypeError("""@delegate object Foo""")
   }

   test("Delegating a top-level class should fail.") {
      assertTypeError("""@delegate class Foo""")
   }

   test("Delegating a method with arguments should fail.") {
      assertTypeError("""class Foo { @delegate def foo(i : Int) = i }""")
   }

   test("Delegating to methods should work.") {
      assertCompiles("""
      class Foo {
         @delegate def foo : Int = 5
      }
      """)
   }

   case class User(name : String, password : String)
   test("Delegating should work in case classes.") {
      assertCompiles("""
      case class Admin(@delegate underlying : User, privileges : List[String])
      Admin(User("Bob", "12345"), Nil).name
      """)
   }

   test("Delegating to members should work.") {
      assertCompiles("""
      class Foo {
         @delegate[Int] val foo : Int = 5
      }
      """)
   }

   test("Delegating to parameters should work.") {
      assertCompiles("""
      class Foo(@delegate[Int] foo : Int)
      """)
   }

   class Foo { def identity[A](a : A) : A = a }
   test("Delegated methods should retain type arguments.") {
      class Bar1(@delegate foo : Foo)
      abstract class Bar2 { @delegate val foo : Foo }
      abstract class Bar3 { @delegate def foo : Foo }
      assertCompiles("""
      val bar1 = (new Bar1(new Foo)).identity[Int](5)
      val bar2 = (new Bar2 { val foo = new Foo }).identity[Int](5)
      val bar3 = (new Bar3 { def foo = new Foo }).identity[Int](5)
      """)
   }

   test("Delegated methods should work correctly for primitive types.") {
      class Foo1(@delegate foo : Int)
      abstract class Foo2 { @delegate val foo : Int }
      abstract class Foo3 { @delegate def foo : Int }
      val five1 = new Foo1(5)
      val five2 = new Foo2 { val foo = 5 }
      val five3 = new Foo3 { def foo = 5 }
      assert((five1 + 1) == 6)
      assert((five2 + 1) == 6)
      assert((five3 + 1) == 6)
   }

   final class Body(display : Option[String] = None) {

      def newBody(newDisplay : Option[String]) = new Body(newDisplay)

      def extractUrls() : List[String] =
         for {
            document ← display.toList
         } yield document
   }

   test("Delegated methods should handle parameterized types.") {
      final case class PostContent(
            headline : Option[String] = None,
            @delegate body : Body = new Body(),
            source : Option[String] = Some("WYSIWYG"),
            images : List[String] = List[String](),
            videos : List[String] = List[String](),
            videoThumbs : List[String] = List[String]()) {
         def extractUrls() : List[String] = videos
      }
   }

   class Foo2 { def identity[A[_], B](a : A[B]) : A[B] = a }
   test("Delegated methods should handle higher kinded types.") {
      class Bar1(@delegate foo : Foo2)
      abstract class Bar2 { @delegate val foo : Foo2 }
      abstract class Bar3 { @delegate def foo : Foo2 }
      assertCompiles("""
      val bar1 = (new Bar1(new Foo)).identity[Int](5)
      val bar2 = (new Bar2 { val foo = new Foo2 }).identity[Option, Int](Option(5))
      val bar3 = (new Bar3 { def foo = new Foo2 }).identity[Option, Int](Option(5))
      """)
   }
}
