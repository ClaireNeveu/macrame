import language.experimental.macros
import reflect.macros.Context
import macrame.{ internal ⇒ fn }

package object macrame {

   /**
    * Logs the source code of the given expression to the console during
    * compliation.
    */
   def trace[A](a : A) : A = macro Impl.trace[A]

   /** A list of all members of type `T` in the given object. */
   def members[T](obj : Object) : List[T] = macro Impl.members[T]

   /**
    * A map of all members of type `T` in the given object, keyed by the name of the member.
    */
   def memberMap[F](obj : Object) : Map[String, F] = macro Impl.memberMap[F]

   implicit class RegexStringContext(sc : StringContext) {
      def r(args : Any*) : scala.util.matching.Regex = macro Impl.regex
   }

   private object Impl {

      def trace[A](c : Context)(a : c.Expr[A]) : c.Expr[A] = {
         import c.universe._
         c.info(
            a.tree.pos,
            "trace output\n   " + show(a.tree) + "\nfor position:\n",
            true)
         a
      }

      def regex(c : Context)(args : c.Expr[Any]*) : c.Expr[scala.util.matching.Regex] = {
         import c.universe._

         val s = c.prefix.tree match {
            case Apply(_, List(Apply(_, rawParts))) ⇒ rawParts
            case x                                  ⇒ c.abort(c.enclosingPosition, "unexpected tree: " + show(x))
         }

         val parts = s map {
            case Literal(Constant(const : String)) ⇒ const
         }

         def getPoint(msg : String) : Int =
            msg.split("\n")(2).indexOf('^')

         try {
            val emptyString : Tree = Literal(Constant(""))

            val placeHolders = List.fill(args.length)(
               java.util.regex.Pattern.quote("placeholder"))

            // Check if the regex is valid.
            try {
               val regex = parts.zipAll(placeHolders, "", "").foldLeft("") {
                  case (a, (b, c)) ⇒ a + b + c
               }
               regex.r
            } catch {
               case e : java.util.regex.PatternSyntaxException ⇒
                  val pos = s.head.pos.withPoint(getPoint(e.getMessage) + s.head.pos.point)
                  c.abort(pos, "Invalid Regex: " + e.getMessage.split("\n").head)
            }

            val mixed : List[(Tree, Tree)] = s.zipAll(
               args.map(p ⇒ q"java.util.regex.Pattern.quote(${p.tree})"),
               emptyString,
               emptyString)
            val regexString = mixed.foldLeft(emptyString) {
               case (a, (b, c)) ⇒ q"$a + $b + $c"
            }
            c.Expr[scala.util.matching.Regex](q"""($regexString).r""")
         } catch {
            case e : Throwable ⇒
               c.abort(c.prefix.tree.pos, e.getMessage)
         }
      }

      def members[T : c.WeakTypeTag](c : Context)(obj : c.Expr[Object]) : c.Expr[List[T]] =
         fn.sequenceExpr(c)(
            fn.members[T](c)(obj)
               .map(s ⇒ fn.renderName(s.name))
               .map(n ⇒ c.Expr[T](c.universe.Select(obj.tree, c.universe.newTermName(n))))
         )

      def memberMap[T : c.WeakTypeTag](c : Context)(obj : c.Expr[Object]) : c.Expr[Map[String, T]] = {
         import c.universe._

         val tups = fn.sequenceExpr(c)(fn.members(c)(obj)
            .map(_.name.decodedName.toString.trim)
            .map(n ⇒
               // ("n", obj.n)
               c.Expr[(String, T)](
                  Apply(Select(Ident(newTermName("Tuple2")), newTermName("apply")), List(
                     Literal(Constant(n)),
                     Select(obj.tree, newTermName(n)))
                  )
               )
            )
         )
         // List(("a", obj.a), ("b", obj.b), ...).toMap
         reify { tups.splice.toMap }
      }
   }
}
