package macrame.enums

import macrame.EnumApi

/**
 * This trait provides conversion from an enumeration to String. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends AsString[Color]
 * }}}
 * This enables the following two ways to convert `Color` to a `String`.
 * {{{
 * Red.asString
 * Color.asString(Red) // returns "Red"
 * }}}
 */
trait AsString[Enum] { self : EnumApi[Enum] ⇒
   @inline def asString(enum : Enum) : String = asStringImpl(enum)
   implicit class AsStringOps(enum : Enum) {
      @inline def asString : String = self.asString(enum)
   }
}

/**
 * This trait provides conversion from String to an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromString[Color]
 * }}}
 * This creates the following function to convert `String` to `Option[Color].
 * {{{
 * Color.fromString("Red") // returns Some(Red)
 * }}}
 */
trait FromString[Enum] { self : EnumApi[Enum] ⇒
   @inline def fromString(str : String) : Option[Enum] = fromStringImpl(str)
}

/**
 * This trait provides the `String`/`Enum` conversions from `AsString` and `FromString`.
 * As with those traits you must extend the companion object of the enumeration.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends StringConverters[Color]
 * }}}
 */
trait StringConverters[Enum] extends AsString[Enum] with FromString[Enum] { self : EnumApi[Enum] ⇒ }

/**
 * This trait provides conversion from an enumeration to Int. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends AsInt[Color]
 * }}}
 * This enables the following two ways to convert `Color` to a `Int`.
 * {{{
 * Red.asInt
 * Color.asInt(Red) // returns 0
 * }}}
 */
trait AsInt[Enum] { self : EnumApi[Enum] ⇒
   @inline def asInt(enum : Enum) : Int = asIntImpl(enum)
   implicit class AsIntOps(enum : Enum) {
      @inline def asInt : Int = self.asInt(enum)
   }
}

/**
 * This trait provides conversion from Int to an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromInt[Color]
 * }}}
 * This creates the following function to convert `Int` to `Option[Color].
 * {{{
 * Color.fromInt(0) // returns Some(Red)
 * }}}
 */
trait FromInt[Enum] { self : EnumApi[Enum] ⇒
   @inline def fromInt(int : Int) : Option[Enum] = fromIntImpl(int)
}

/**
 * This trait provides the `Int`/`Enum` conversions from `AsInt` and `FromInt`.
 * As with those traits you must extend the companion object of the enumeration.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends IntConverters[Color]
 * }}}
 */
trait IntConverters[Enum] extends AsInt[Enum] with FromInt[Enum] { self : EnumApi[Enum] ⇒ }

/**
 * This trait provides conversion from an enumeration to Short. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends AsShort[Color]
 * }}}
 * This enables the following two ways to convert `Color` to a `Short`.
 * {{{
 * Red.asShort
 * Color.asShort(Red) // returns 0
 * }}}
 */
trait AsShort[Enum] { self : EnumApi[Enum] ⇒
   @inline def asShort(enum : Enum) : Short = asShortImpl(enum)
   implicit class AsShortOps(enum : Enum) {
      @inline def asShort : Short = self.asShort(enum)
   }
}

/**
 * This trait provides conversion from Short to an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromShort[Color]
 * }}}
 * This creates the following function to convert `Short` to `Option[Color].
 * {{{
 * Color.fromShort(0 : Short) // returns Some(Red)
 * }}}
 */
trait FromShort[Enum] { self : EnumApi[Enum] ⇒
   @inline def fromShort(short : Short) : Option[Enum] = fromShortImpl(short)
}

/**
 * This trait provides the `Short`/`Enum` conversions from `AsShort` and `FromShort`.
 * As with those traits you must extend the companion object of the enumeration.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends ShortConverters[Color]
 * }}}
 */
trait ShortConverters[Enum] extends AsShort[Enum] with FromShort[Enum] { self : EnumApi[Enum] ⇒ }

/**
 * This trait provides conversion from an enumeration to Long. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends AsLong[Color]
 * }}}
 * This enables the following two ways to convert `Color` to a `Long`.
 * {{{
 * Red.asLong
 * Color.asLong(Red) // returns 0l
 * }}}
 */
trait AsLong[Enum] { self : EnumApi[Enum] ⇒
   @inline def asLong(enum : Enum) : Long = asLongImpl(enum)
   implicit class AsLongOps(enum : Enum) {
      @inline def asLong : Long = self.asLong(enum)
   }
}

/**
 * This trait provides conversion from Long to an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends FromLong[Color]
 * }}}
 * This creates the following function to convert `Long` to `Option[Color].
 * {{{
 * Color.fromLong(0l) // returns Some(Red)
 * }}}
 */
trait FromLong[Enum] { self : EnumApi[Enum] ⇒
   @inline def fromLong(long : Long) : Option[Enum] = fromLongImpl(long)
}

/**
 * This trait provides the `Long`/`Enum` conversions from `AsLong` and `FromLong`.
 * As with those traits you must extend the companion object of the enumeration.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends LongConverters[Color]
 * }}}
 */
trait LongConverters[Enum] extends AsLong[Enum] with FromLong[Enum] { self : EnumApi[Enum] ⇒ }

/**
 * This trait provides the numeric/enumeration conversions from `IntConverters`, `ShortConverters`, and `LongConverters`.
 * As with those traits you must extend the companion object of the enumeration.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends NumericConverters[Color]
 * }}}
 */
trait NumericConverters[Enum] extends LongConverters[Enum] with ShortConverters[Enum] with IntConverters[Enum] { self : EnumApi[Enum] ⇒ }

/**
 * This trait provides an `Ordering` instance for an enumeration as well
 * as functions which use that order. It works by extending the companion
 * object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends Ordered[Color]
 * }}}
 * This enables the following functions (and more).
 * {{{
 * Red < Blue // true
 * Blue.next
 * Color.next(Blue) // yellow
 * Yellow >= Yellow // true
 * Color.first // Red
 * Color.last // Yellow
 * }}}
 */
trait Ordered[Enum] { self : EnumApi[Enum] ⇒
   @inline val ordering : Ordering[Enum] = orderingImpl
   @inline def next(enum : Enum) : Option[Enum] = nextImpl(enum)
   @inline def prev(enum : Enum) : Option[Enum] = prevImpl(enum)
   @inline def first : Enum = firstImpl
   @inline def last : Enum = lastImpl
   implicit class OrderedOps(enum : Enum) {
      @inline def next : Option[Enum] = self.next(enum)
      @inline def prev : Option[Enum] = self.prev(enum)
      @inline def <(other : Enum) = ordering.lt(enum, other)
      @inline def <=(other : Enum) = ordering.lteq(enum, other)
      @inline def >(other : Enum) = ordering.gt(enum, other)
      @inline def >=(other : Enum) = ordering.gteq(enum, other)
   }
}

/**
 * This traits provides the same interface as `Ordered` as well as the modular
 * ordering functions. As with that trait it works by extending the companion
 * object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends OrderedModular[Color]
 * }}}
 * This enables the following functions.
 * {{{
 * Red.prevMod
 * Color.prevMod(Red) // Yellow
 * Yellow.nextMod
 * Color.nextMod(Yellow) // Red
 * }}}
 */
trait OrderedModular[Enum] extends Ordered[Enum] { self : EnumApi[Enum] ⇒
   @inline def nextMod(enum : Enum) : Enum = nextModImpl(enum)
   @inline def prevMod(enum : Enum) : Enum = prevModImpl(enum)
   implicit class OrderedModularOps(enum : Enum) {
      @inline def nextMod : Enum = self.nextMod(enum)
      @inline def prevMod : Enum = self.prevMod(enum)
   }
}

/**
 * This traits provides *all* of the auto-generated functions in `EnumApi`.
 * It is rare that one actually needs all such functions and it is recommended
 * that you use a smaller subset of these functions as provided by the other traits
 * in the `macrame.enums` namespace.
 */
trait All[Enum] extends NumericConverters[Enum] with OrderedModular[Enum] { self : EnumApi[Enum] ⇒ }
