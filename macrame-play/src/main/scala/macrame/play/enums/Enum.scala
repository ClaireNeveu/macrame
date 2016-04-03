package macrame.play.enums

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
 * If you need your `Reads` instance to be case-insensitive you can override
 * the `caseSensitive` member like so:
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromJson[Color] {
 *   override val caseSensitive = false
 * }
 * }}}
 */
trait FromJson[Enum] { self : EnumApi[Enum] ⇒
   /** Indicates whether `reads` is case sensitive. */
   protected val caseSensitive = true
   implicit lazy val reads : Reads[Enum] =
      if (caseSensitive)
         new Reads[Enum] {
            def reads(js : JsValue) : JsResult[Enum] =
               js.asOpt[String]
                  .flatMap(fromStringImpl)
                  .fold[JsResult[Enum]](JsError(s"Expected $className but found: $js"))(JsSuccess(_))
         }
      else
         new Reads[Enum] {
            def reads(js : JsValue) : JsResult[Enum] = {
               val in = js.asOpt[String].map(_.toLowerCase)
               valuesImpl.map(v ⇒ asStringImpl(v).toLowerCase -> v)
                  .find(v ⇒ in.exists(_ == v._1))
                  .fold[JsResult[Enum]](JsError(s"Expected $className but found: $js"))(e ⇒ JsSuccess(e._2))
            }
         }

}

/**
 * This trait provides an instance of `Reads` for an enumeration. Unlike
 * `FromJson`, the `Reads` instance created by this trait operates on
 * `JsNumber` using the `Int` representation of the enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromJsonNumeric[Color]
 * }}}
 * Allows you to read JSON numbers as Color like so:
 * {{{
 * JsNumber(0).validate[Color] // JsResult(Red)
 * }}}
 */
trait FromJsonNumeric[Enum] { self : EnumApi[Enum] ⇒
   implicit lazy val reads : Reads[Enum] =
      new Reads[Enum] {
         def reads(js : JsValue) : JsResult[Enum] =
            js.asOpt[Int]
               .flatMap(fromIntImpl)
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
 * If you need your `Format` instance to be case-insensitive you can override
 * the `caseSensitive` member like so:
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends JsonConverters[Color] {
 *   override val caseSensitive = false
 * }
 * }}}
 */
trait JsonConverters[Enum] extends AsJson[Enum] with FromJson[Enum] { self : EnumApi[Enum] ⇒ }
