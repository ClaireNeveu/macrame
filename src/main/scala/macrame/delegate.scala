package macrame

import macrame.internal.renderName

import scala.reflect.macros.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

class delegate extends StaticAnnotation {
   def macroTransform(annottees : Any*) = macro delegate.impl
}

object delegate {

   def impl(c : Context)(annottees : c.Expr[Any]*) : c.Expr[Any] = {
      import c.universe._
      import Flag._

      val (delegate : Tree, container : Option[Tree], companion : Option[Tree]) = annottees match {
         case delegate :: clazz :: obj :: Nil ⇒ (delegate.tree, Some(clazz.tree), Some(obj.tree))
         case delegate :: clazz :: Nil        ⇒ (delegate.tree, Some(clazz.tree), None)
         case delegate :: Nil                 ⇒ (delegate.tree, None, None)
      }
      container match {
         case Some(tree) ⇒ parameterImpl(c)(delegate, tree, companion)
         case None       ⇒ methodImpl(c)(delegate)
      }
   }

   def methodImpl(c : Context)(delegate : c.Tree) : c.Expr[Any] = {

      import c.universe._
      import Flag._

      def getType(tpt : Tree) : Type =
         c.typeCheck(q"""(throw new java.lang.Exception("")) : $tpt""").tpe

      val (delegateName, delegateType) = delegate match {
         case ValDef(_, name, tpt, _) ⇒
            name -> getType(tpt)
         case DefDef(_, name, tparams, vparamss, tpt, _) if tparams.isEmpty && vparamss.isEmpty ⇒
            name -> getType(tpt)
         case _ ⇒
            c.abort(NoPosition, "Delegate must be a parameter or method with no arguments.")
      }

      val containerType = c.typeOf[AnyRef]

      val delegatedMembers : List[Tree] = delegateType.members.filter(s ⇒
         (containerType.member(s.name) == NoSymbol) &&
            (s.name.decoded != "<init>")
      ).collect {
         case method : MethodSymbol ⇒
            val arguments = method.paramss.map(_.map(p ⇒
               ValDef(
                  Modifiers(PARAM),
                  p.name.toTermName,
                  Ident(p.typeSignature.typeSymbol.name.toTypeName),
                  EmptyTree)))
            val methodName = method.name.toTermName
            val typeArgs = method.typeParams.map(t ⇒
               TypeDef(
                  Modifiers(PARAM),
                  t.name.toTypeName,
                  List(),
                  TypeBoundsTree(tq"${c.typeOf[Nothing]}", tq"${c.typeOf[Any]}")))
            val passedTypeArgs = method.typeParams.map(t ⇒ Ident(t.name.toTypeName))

            val methodCall =
               if (passedTypeArgs.nonEmpty)
                  TypeApply(
                     Select(
                        Ident(delegateName),
                        methodName),
                     passedTypeArgs)
               else
                  Select(
                     Ident(delegateName),
                     methodName)

            val rhs = arguments.foldLeft[Tree](methodCall) {
               case (tree, args) ⇒ Apply(tree, args.map(v ⇒ Ident(v.name)))
            }
            DefDef(
               Modifiers(),
               methodName,
               typeArgs,
               arguments,
               TypeTree(),
               rhs)
      }.toList

      //delegatedMembers.foreach(println)
      c.Expr[Any](Block(delegate :: delegatedMembers, Literal(Constant(()))))
   }

   def parameterImpl(c : Context)(
      delegate : c.Tree,
      container : c.Tree,
      companion : Option[c.Tree]) : c.Expr[Any] = {

      import c.universe._
      import Flag._

      def mkTypeDef(symbol : TypeSymbol) : TypeDef = {
         val (lo : Type, hi : Type) = symbol.typeSignature match {
            case t : BoundedWildcardType ⇒ (t.bounds.lo, t.bounds.hi)
            case _                       ⇒ (c.typeOf[Nothing], c.typeOf[Any])
         }
         TypeDef(
            Modifiers(PARAM),
            symbol.name.toTypeName,
            symbol.typeParams.map(t ⇒ mkTypeDef(t.asType)),
            TypeBoundsTree(TypeTree(lo), TypeTree(hi)))
      }

      def mkDefDef(symbol : MethodSymbol, mods : Modifiers, rhs : Tree) : DefDef =
         DefDef(
            mods,
            symbol.name.toTermName,
            symbol.typeParams.map(p ⇒ mkTypeDef(p.asType)),
            symbol.typeSignature.paramss.map(_.map(p ⇒ mkValDef(p.asTerm, Modifiers(PARAM), EmptyTree))),
            TypeTree(symbol.typeSignature.finalResultType),
            rhs)

      def mkValDef(symbol : TermSymbol, mods : Modifiers, rhs : Tree) : ValDef = {
         ValDef(
            mods,
            symbol.name.toTermName,
            TypeTree(symbol.typeSignature),
            rhs)
      }

      def getType(tpt : Tree) : Type =
         c.typeCheck(q"""(throw new java.lang.Exception("")) : $tpt""").tpe

      val (delegateName, delegateType) = delegate match {
         case ValDef(_, name, tpt, _) ⇒
            name -> getType(tpt)
         case _ ⇒
            c.abort(NoPosition, "Delegate must be a parameter or method.")
      }
      val output = container match {
         case ClassDef(mods, containerName, tparams, impl) ⇒
            val existingMembers = impl.body.flatMap {
               case DefDef(_, name, _, _, _, _) ⇒ List(name)
               case ValDef(_, name, _, _)       ⇒ List(name)
               case t                           ⇒ Nil
            }
            val containerType = c.typeCheck(q"""(throw new java.lang.Exception("")) : Object with ..${impl.parents}""").tpe
            val delegatedMembers = delegateType.members.filter(s ⇒
               !existingMembers.contains(s.name) &&
                  (containerType.member(s.name) == NoSymbol) &&
                  (s.name.decoded != "<init>")
            ).collect {
               case method : MethodSymbol ⇒
                  val arguments = method.paramss.map(_.map(_.name.toTermName))
                  val methodName = method.name.toTermName
                  val passedTypeArgs = method.typeParams.map(t ⇒ Ident(t.name.toTypeName))

                  val methodCall =
                     if (passedTypeArgs.nonEmpty)
                        TypeApply(
                           Select(
                              Ident(delegateName),
                              methodName),
                           passedTypeArgs)
                     else
                        Select(
                           Ident(delegateName),
                           methodName)

                  val rhs = arguments.foldLeft[Tree](methodCall) {
                     case (tree, args) ⇒ Apply(tree, args.map(v ⇒ Ident(v)))
                  }
                  mkDefDef(method, Modifiers(), rhs)
            }

            ClassDef(mods, containerName, tparams, Template(impl.parents, impl.self, impl.body ++ delegatedMembers))
         case _ ⇒ c.abort(NoPosition, "Delegate must be a parameter or method of a class.")
      }
      println(output)
      c.Expr[Any](Block(output :: companion.toList, Literal(Constant(()))))
   }
}
