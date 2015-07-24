package macrame

import macrame.internal.renderName

import scala.reflect.macros.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class enum extends StaticAnnotation {
   def macroTransform(annottees : Any*) = macro enum.impl
}

object enum {
   def impl(c : Context)(annottees : c.Expr[Any]*) : c.Expr[Any] = {
      import c.universe._
      import Flag._

      def zipWithIndex[A](as : List[A]) : List[(Int, A)] = {
         var i = -1
         as.map { a ⇒
            i = i + 1
            i -> a
         }
      }

      val (input : Tree, companion : Option[Tree]) = annottees match {
         case clazz :: obj :: Nil ⇒ (clazz.tree, Some(obj.tree))
         case clazz :: Nil        ⇒ (clazz.tree, None)
         case _                   ⇒ c.abort(NoPosition, "Enum must be a class.")
      }
      val outputs = input match {
         case ClassDef(mods, enumName, tparams, impl) ⇒
            impl.body.foreach {
               case Ident(_) ⇒
               case DefDef(_, name, _, _, _, _) if name.decoded == "<init>" ⇒
               case t ⇒
                  println(showRaw(t))
                  c.abort(t.pos, "An enum may only contain identifiers.")
            }
            val init = impl.body.find {
               case DefDef(_, name, _, _, _, _) if name.decoded == "<init>" ⇒ true
            }.get

            val cases : List[Name] = impl.body.collect {
               case Ident(name) ⇒ name
            }

            val Enum = enumName.toTypeName

            val caseObjects = cases.map { name ⇒
               val enumType = enumName.toTypeName
               q"""case object ${name.toTermName} extends $enumType"""
            }

            val asString = {
               val caseDefs : List[CaseDef] = cases.map { name ⇒
                  val lit = Literal(Constant(renderName(name)))
                  cq"""`${name.toTermName}` ⇒ $lit"""
               }

               q"""protected def asStringImpl(e : $Enum) = e match {
                  case ..$caseDefs
               }"""
            }

            val fromString = {
               val caseDefs : List[CaseDef] = cases.map { name ⇒
                  val lit = Literal(Constant(renderName(name)))
                  cq"""$lit ⇒ Some(${name.toTermName})"""
               }

               q"""protected def fromStringImpl(s : String) : Option[$Enum] = s match {
                  case ..$caseDefs
                  case _ ⇒ None
               }"""
            }

            val indexedCases = zipWithIndex(cases)

            val asInt = {
               val caseDefs : List[CaseDef] = indexedCases map {
                  case (i, name) ⇒ cq"`${name.toTermName}` ⇒ $i"
               }

               q"""protected def asIntImpl(e : $Enum) = e match {
                  case ..$caseDefs
               }"""
            }

            val fromInt = {
               val caseDefs : List[CaseDef] = indexedCases map {
                  case (i, name) ⇒ cq"$i ⇒ Some(${name.toTermName})"
               }

               q"""protected def fromIntImpl(i : Int) : Option[$Enum] = i match {
                  case ..$caseDefs
                  case _ ⇒ None
               }"""
            }

            val first = q"protected def firstImpl : $Enum = ${cases.head.toTermName}"
            val last = q"protected def lastImpl : $Enum = ${cases.last.toTermName}"

            val apiImpl = List(
               asString,
               fromString,
               asInt,
               fromInt,
               first,
               last)

            val enumApi = tq"macrame.EnumApi[$Enum]"

            val companionObj = companion match {
               case Some(ModuleDef(mods, objName, objImpl)) ⇒
                  val newParents = enumApi :: (objImpl.parents.filter(_ == tq"scala.AnyRef"))
                  ModuleDef(
                     mods,
                     objName,
                     Template(
                        newParents,
                        objImpl.self,
                        caseObjects ++ apiImpl ++ objImpl.body))
               case None ⇒
                  val newParents = enumApi :: (impl.parents.filter(_ == tq"scala.AnyRef"))
                  ModuleDef(
                     Modifiers(),
                     enumName.toTermName,
                     Template(newParents, impl.self, init :: caseObjects ++ apiImpl))
            }
            List(
               q"sealed abstract class ${enumName.toTypeName} extends Product with Serializable",
               companionObj)
         case _ ⇒ c.abort(NoPosition, "Enum must be a class.")
      }
      // outputs.foreach(println)
      c.Expr[Any](Block(outputs, Literal(Constant(()))))
   }
}
