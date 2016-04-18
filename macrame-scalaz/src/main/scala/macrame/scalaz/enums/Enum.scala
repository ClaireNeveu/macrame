package macrame.scalaz.enums

import macrame.EnumApi

import scala.annotation.tailrec

import scalaz._

/**
 * This trait provides an instance of `Show` for an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Color {
 *   Red
 *   Blue
 *   Yellow
 * }
 * object Color extends IsShow[Color]
 * }}}
 */
trait IsShow[Enum] { self : EnumApi[Enum] ⇒
   implicit val show : Show[Enum] = Show.shows[Enum](asStringImpl _)
}

trait IsEqual[Enum] { self : EnumApi[Enum] ⇒
   implicit val equal : Equal[Enum] = Equal.equal[Enum](_ == _)
}

trait IsOrder[Enum] { self : EnumApi[Enum] ⇒
   implicit val order : Order[Enum] = new Order[Enum] {
      override def equal(x : Enum, y : Enum) : Boolean = x == y
      def order(x : Enum, y : Enum) : Ordering =
         if (x == y)
            Ordering.EQ
         else if (orderingImpl.gt(x, y))
            Ordering.GT
         else
            Ordering.LT
   }
}

trait IsEnum[E] { self : EnumApi[E] ⇒
   implicit val enum : Enum[E] = new Enum[E] {
      override def equal(x : E, y : E) : Boolean = x == y
      def order(x : E, y : E) : Ordering =
         if (x == y)
            Ordering.EQ
         else if (orderingImpl.gt(x, y))
            Ordering.GT
         else
            Ordering.LT
      def pred(e : E) = prevModImpl(e)
      def succ(e : E) = nextModImpl(e)
      override val max = Some(lastImpl)
      override val min = Some(firstImpl)
   }
}

/**
 * This trait provides an instance of `Semigroup` for an enumeration. It works
 * by extending the companion object of the enumeration class.
 * {{{
 * @enum class Mod3 {
 *   Zero
 *   One
 *   Two
 * }
 * object Mod3 extends IsSemigroup[Mod3]
 * }}}
 */
trait IsSemigroup[Enum] { self : EnumApi[Enum] ⇒
   implicit lazy val semigroup : Semigroup[Enum] = new Semigroup[Enum] {
      def append(x : Enum, y : ⇒ Enum) : Enum = nextN(x, asIntImpl(y))

      @tailrec
      private def nextN(e : Enum, n : Int) : Enum =
         if (n == 0)
            e
         else
            nextN(nextModImpl(e), n - 1)
   }
}

trait IsMonoid[Enum] { self : EnumApi[Enum] ⇒
   implicit lazy val monoid : Monoid[Enum] = new Monoid[Enum] {
      val zero : Enum = firstImpl
      def append(x : Enum, y : ⇒ Enum) : Enum = nextN(x, asIntImpl(y))

      @tailrec
      private def nextN(e : Enum, n : Int) : Enum =
         if (n <= 0)
            e
         else
            nextN(nextModImpl(e), n - 1)
   }
}

trait All[Enum] extends IsMonoid[Enum] with IsEnum[Enum] with IsShow[Enum] { self : EnumApi[Enum] ⇒ }
