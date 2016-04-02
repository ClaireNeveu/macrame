package macrame.play

import macrame.EnumApi

import play.api.libs.json._

/**
 * This trait provides an instance of `Writes` for an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends AsJson[Color]
 * }}}
 */
trait AsJson[Enum] { self : EnumApi[Enum] ⇒
   implicit val writes : Writes[Enum] = new Writes[Enum] {
      def writes(enum : Enum) : JsValue = JsString(asStringImpl(enum))
   }
}

/**
 * This trait provides an instance of `Reads` for an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromJson[Color]
 * }}}
 */
trait FromJson[Enum] { self : EnumApi[Enum] ⇒
   lazy val className = this.getClass.getName.stripSuffix("$")
   implicit lazy val reads : Reads[Enum] = new Reads[Enum] {
      def reads(js : JsValue) : JsResult[Enum] =
         js.asOpt[String]
            .flatMap(fromStringImpl)
            .fold[JsResult[Enum]](JsError(s"Expected $className but found: $js"))(JsSuccess(_))
   }
}

/**
 * This trait provides an instance of `Format` for an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends JsonConverters[Color]
 * }}}
 */
trait JsonConverters[Enum] extends AsJson[Enum] with FromJson[Enum] { self : EnumApi[Enum] ⇒ }
