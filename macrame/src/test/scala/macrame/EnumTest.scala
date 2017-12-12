package macrame

import org.scalatest.FunSuite

import scala.language.implicitConversions
import scala.math.Ordering

class EnumTest extends FunSuite {

   trait EnumExtension[A] { self : EnumApi[A] ⇒
      def foo : A ⇒ String = a ⇒ asStringImpl(a)
   }

   test("Passing a non-String literal to a case should fail.") {
      assertTypeError("""@enum class Color { Red(5) }""")
   }

   test("Passing a non-String value to a case should fail.") {
      val foo = 5
      assertTypeError("""@enum class Color { Red(foo) }""")
   }

   test("The simple case should work.") {
      assertCompiles("""
      @enum class Color {
         Red
         Blue
         Yellow
      }
      """)
   }

   test("Overriding the string representation should work.") {
      val yellowStr = "YELLOW"
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow(yellowStr)
      }
      object Color {
         val asString = asStringImpl _
         val fromString = fromStringImpl _
      }
      assert(Color.asString(Color.Red) == "Red")
      assert(Color.asString(Color.Blue) == "BLUE")
      assert(Color.asString(Color.Yellow) == "YELLOW")
      assert(Color.fromString("Red") == Some(Color.Red))
      assert(Color.fromString("BLUE") == Some(Color.Blue))
      assert(Color.fromString("YELLOW") == Some(Color.Yellow))
   }

   test("Int representations should work.") {
      @enum class Color {
         Red
         Blue
         Yellow
      }
      object Color {
         val asInt = asIntImpl _
         val fromInt = fromIntImpl _
      }
      assert(Color.asInt(Color.Red) == 0)
      assert(Color.asInt(Color.Blue) == 1)
      assert(Color.asInt(Color.Yellow) == 2)
      assert(Color.fromInt(0) == Some(Color.Red))
      assert(Color.fromInt(1) == Some(Color.Blue))
      assert(Color.fromInt(2) == Some(Color.Yellow))
   }

   test("Traits should be able to extend EnumApi.") {
      @enum class Color {
         Red
         Blue
         Yellow
      }
      object Color extends EnumExtension[Color] {
      }
      assert(Color.foo(Color.Red) == "Red")
      assert(Color.foo(Color.Blue) == "Blue")
      assert(Color.foo(Color.Yellow) == "Yellow")
   }

   test("values should be correct.") {
      @enum class Color {
         Red
         Blue
         Yellow
      }
      object Color {
         val values = valuesImpl
      }
      assert(Color.values == Set(Color.Red, Color.Blue, Color.Yellow))
   }

   test("className should be correct.") {
      @enum class Color {
         Red
         Blue
         Yellow
      }
      object Color {
         val name = className
      }
      assert(Color.name == "Color")
   }

   val yellowStr = "YELLOW"

   @enum class TrafficColor {
      Red("RED")
      Yellow(yellowStr)
      Green
      Blue
   }
   object TrafficColor {
      def asString(tc : TrafficColor) = asStringImpl(tc)
      def fromString(str : String) = fromStringImpl(str)
      def asShort(tc : TrafficColor) = asShortImpl(tc)
      def fromShort(short : Short) = fromShortImpl(short)
      def asInt(tc : TrafficColor) = asIntImpl(tc)
      def fromInt(int : Int) = fromIntImpl(int)
      def asLong(tc : TrafficColor) = asLongImpl(tc)
      def fromLong(long : Long) = fromLongImpl(long)
      def next(tc : TrafficColor) = nextImpl(tc)
      def nextMod(tc : TrafficColor) = nextModImpl(tc)
      def prev(tc : TrafficColor) = prevImpl(tc)
      def prevMod(tc : TrafficColor) = prevModImpl(tc)
      val first : TrafficColor = firstImpl
      val last : TrafficColor = lastImpl
      implicit val ordering : Ordering[TrafficColor] = orderingImpl

      val values : List[TrafficColor] = members[TrafficColor](this)
   }
}
