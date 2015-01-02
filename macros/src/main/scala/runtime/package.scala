package macrame

import scala.reflect.runtime.universe._

package object runtime {
   /**
    * Retrieves all members of type T on object obj.
    */
   def members[T : TypeTag](obj : Object) : List[T] = {
      val instanceMirror = runtimeMirror(obj.getClass.getClassLoader).reflect(obj)
      instanceMirror.symbol.typeSignature.members
         .filter(_.typeSignature <:< typeOf[T])
         .collect {
            case s : TermSymbol ⇒ s
         }
         .map { symbol ⇒
            val size = instanceMirror.reflectField(symbol.asTerm).get match {
               case f : T @unchecked if f.isInstanceOf[T] ⇒ f
            }
            size
         }.toList
   }
   /**
    * Retrieves all members of type T on object obj and maps them from their names.
    */
   def memberMap[T : TypeTag](obj : Object) : Map[String, T] = {
      val instanceMirror = runtimeMirror(obj.getClass.getClassLoader).reflect(obj)
      instanceMirror.symbol.typeSignature.members
         .filter(_.typeSignature <:< typeOf[T])
         .collect {
            case s : TermSymbol ⇒ s
         }
         .map { symbol ⇒
            val name = symbol.name.toString.stripSuffix(" ")
            val size = instanceMirror.reflectField(symbol.asTerm).get match {
               case f : T @unchecked if f.isInstanceOf[T] ⇒ f
            }
            (name, size)
         }.toMap
   }
}
