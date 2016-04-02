package macrame

import scala.math.Ordering

/**
 * The API exposed by enumerations created with the @enum macro.
 * Unlike Scala's Enumeration interface, all auto-generated functions
 * are protected so the user can choose which functions to expose on
 * their type.
 *
 * @tparam Enum The type of the enumeration.
 */
trait EnumApi[Enum] {
   /** Returns a string representation of the enum. */
   protected def asStringImpl(enum : Enum) : String

   /** Returns the case whose String representation matches, if there is one. */
   protected def fromStringImpl(str : String) : Option[Enum]

   /**
    * Returns a Short representation of the enum.
    * The first listed case is 1, the second 2, and so on.
    */
   protected def asShortImpl(enum : Enum) : Short = asIntImpl(enum).toShort

   /** Returns the case whose Short representation matches, if there is one. */
   protected def fromShortImpl(short : Short) : Option[Enum] = fromIntImpl(short.toInt)

   /**
    * Returns an Int representation of the enum.
    * The first listed case is 1, the second 2, and so on.
    */
   protected def asIntImpl(enum : Enum) : Int

   /** Returns the case whose Int representation matches, if there is one. */
   protected def fromIntImpl(int : Int) : Option[Enum]

   /**
    * Returns a Long representation of the enum.
    * The first listed case is 1, the second 2, and so on.
    */
   protected def asLongImpl(enum : Enum) : Long = asIntImpl(enum).toLong

   /** Returns the case whose Long representation matches, if there is one. */
   protected def fromLongImpl(long : Long) : Option[Enum] = fromIntImpl(long.toInt)

   /** Returns the case defined just after the given enum, if one exists. */
   protected def nextImpl(enum : Enum) : Option[Enum] =
      fromIntImpl(asIntImpl(enum) + 1)

   /** As `nextImpl` but modular: the last defined case wraps around to the first one. */
   protected def nextModImpl(enum : Enum) : Enum =
      nextImpl(enum) getOrElse firstImpl

   /** Returns the case defined just before the given enum, if one exists. */
   protected def prevImpl(enum : Enum) : Option[Enum] =
      fromIntImpl(asIntImpl(enum) - 1)

   /** As `prevImpl` but modular: the first defined case wraps around to the last one. */
   protected def prevModImpl(enum : Enum) : Enum =
      prevImpl(enum) getOrElse lastImpl

   /** The first case defined in the enum. */
   protected def firstImpl : Enum

   /** The last case defined in the enum. */
   protected def lastImpl : Enum

   /** An instance of `Ordering` based on the definition order of the cases. */
   protected def orderingImpl : Ordering[Enum] = Ordering.by(asShortImpl)
}
