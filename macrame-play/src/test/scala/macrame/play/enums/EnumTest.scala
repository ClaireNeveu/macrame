package macrame.play.enums

import macrame.enum

import org.scalatest.FunSuite

import play.api.libs.json._

import scala.math.Ordering

class EnumTest extends FunSuite {

   test("AsJson should work.") {
      val yellowStr = "YELLOW"
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow(yellowStr)
      }
      object Color extends AsJson[Color]

      assert(Json.toJson(Color.Red) == JsString("Red"))
      assert(Json.toJson(Color.Blue) == JsString("BLUE"))
      assert(Json.toJson(Color.Yellow) == JsString("YELLOW"))
   }

   test("FromJson should work.") {
      val yellowStr = "YELLOW"
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow(yellowStr)
      }
      object Color extends FromJson[Color]

      assert(JsString("Red").as[Color] == Color.Red)
      assert(JsString("BLUE").as[Color] == Color.Blue)
      assert(JsString("YELLOW").as[Color] == Color.Yellow)
   }

   test("FromJsonNumeric should work.") {
      @enum class Color {
         Red
         Blue
         Yellow
      }
      object Color extends FromJsonNumeric[Color]

      assert(JsNumber(0).as[Color] == Color.Red)
      assert(JsNumber(1).as[Color] == Color.Blue)
      assert(JsNumber(2).as[Color] == Color.Yellow)
   }

   test("JsonConverters should work.") {
      val yellowStr = "YELLOW"
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow(yellowStr)
      }
      object Color extends JsonConverters[Color]

      assertCompiles("""implicitly[Format[Color]]""")
   }

   test("Case insensitive FromJson should work.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow("YeLlOw")
      }
      object Color extends FromJson[Color] {
         override val caseSensitive = false
      }

      assert(JsString("RED").as[Color] == Color.Red)
      assert(JsString("blue").as[Color] == Color.Blue)
      assert(JsString("YELLOW").as[Color] == Color.Yellow)
      assert(JsString("yellow").as[Color] == Color.Yellow)
   }
}
