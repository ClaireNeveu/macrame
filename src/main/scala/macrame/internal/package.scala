package macrame

import reflect.macros.blackbox.Context

package object internal {

   /**
    * Finds a method with the given name accessible on the given object in the
    * given Context. This can be a method on the object or a method on a valid
    *  implicit class or implicit conversion target.
    *
    * @param context The context to search in.
    * @param name The name of the method.
    * @param obj The object the method should belong to.
    */
   // def methodWithName(context : Context)(name : String, obj : context.Expr[Object]) = ???

   /**
    * Converts a list of Expr into a single Expr of the form
    * {{{
    * List(expr1, expr2, expr3)
    * }}}
    * @param c The context to create the expression in.
    * @param expressions The expressions to be converted into one.
    */
   def sequenceExpr[T : c.WeakTypeTag](c : Context)(expressions : Traversable[c.Expr[T]]) : c.Expr[List[T]] = {
      import c.universe._
      c.Expr[List[T]](
         Apply(
            Select(Ident(TermName("List")), TermName("apply")),
            expressions.map(_.tree).toList
         )
      )
   }

   /**
    * Selects all members of type T on the given object.
    */
   def members[T : c.WeakTypeTag](c : Context)(obj : c.Expr[Object]) : Iterable[c.universe.Symbol] =
      obj.actualType.members
         .filter(_.typeSignature <:< c.weakTypeOf[T])
         .filter(_.isTerm)

   def renderName(name : Context#Name) : String =
      name.decodedName.toString.trim
}
