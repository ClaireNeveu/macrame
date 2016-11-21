package macrame

import scala.reflect.api.Types

object Foo {
   def setType(nativeArray : Types#Type) : Unit = ???
}

object FooImplicit {
   implicit final class RichAnyRef[A](val underlying : A) {
      def get : A = underlying
   }
}
