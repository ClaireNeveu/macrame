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

   test("Delegated parameters should handle parameterized types.") {
      assertCompiles("""
      final case class PostContent(
            headline : Option[String] = None,
            @delegate body : Body = new Body(),
            source : Option[String] = Some("WYSIWYG"),
            images : List[String] = List[String](),
            videos : List[String] = List[String](),
            videoThumbs : List[String] = List[String]()) {
         def extractUrls() : List[String] = videos
      }
      """)
   }

   case class CaseOne(foo : String, bar : Int, baz : Long)
   test("Delegated parameters should not clobber copy methods.") {
      case class CaseTwo(@delegate one : CaseOne, alpha : Boolean)
      class NonCase(@delegate one : CaseOne, alpha : Boolean)
      val a = CaseOne("a", 1, 2l)
      val b = CaseOne("b", 3, 4l)
      val c = CaseTwo(a, true)
      val d = CaseTwo(b, true)
      val n1 = new NonCase(a, true)
      assert(c.copy(one = b) == d)
      assertTypeError("""n1.copy""")
   }

   test("Delegated members should not clobber copy methods.") {
      abstract class NonCase { @delegate val one : CaseOne; val alpha : Boolean }
      val a = CaseOne("a", 1, 2l)
      val n1 = new NonCase { val one = a; val alpha = true }
      assertTypeError("""n1.copy()""")
   }

   test("Delegated members with path-dependent types should work.") {
      assertCompiles("""
      object Bar {
         @delegate def foo: Foo.type = Foo
      }
      """)
   }

   test("Delegated implicit members should be implicit.") {
      assertCompiles("""
      object BarImplicit {
         @delegate def foo: FooImplicit.type = FooImplicit
      }
      import BarImplicit._
      "xxx".get
      """)
   }

   test("Delegated parameters should handle private members.") {
      final case class Delegate(
            headline : Option[String] = None) {
         private val foo : Int = 5
         private val baz : String = "five"
      }
      assertCompiles("""
      final case class PostContent(
            headline : Option[String] = None,
            @delegate un : Delegate)
      """)
   }
}
