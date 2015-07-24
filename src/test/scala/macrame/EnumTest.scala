package macrame

import scala.math.Ordering

class EnumTest {

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
   }
}
