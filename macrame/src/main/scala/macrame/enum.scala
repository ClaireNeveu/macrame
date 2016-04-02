package macrame

import macrame.internal.renderName

import scala.reflect.macros.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

class enum extends StaticAnnotation {
   def macroTransform(annottees : Any*) = macro enum.impl
}

object enum {

   def impl(c : Context)(annottees : c.Expr[Any]*) : c.Expr[Any] = {
      import c.universe._
      import Flag._

      case class Case(name : TermName, str : Tree)

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
               case Apply(Ident(_), _ :: Nil) ⇒
               case DefDef(_, name, _, _, _, _) if name.decoded == "<init>" ⇒
               case t ⇒
                  // println(showRaw(t))
                  c.abort(t.pos, "Invalid case in Enum declaration.")
            }
            val init = impl.body.find {
               case DefDef(_, name, _, _, _, _) if name.decoded == "<init>" ⇒ true
            }.get

            val cases : List[Case] = impl.body.collect {
               case Apply(Ident(name), str :: Nil) ⇒ Case(name.toTermName, str)
               case Ident(name) ⇒
                  Case(name.toTermName, Literal(Constant(renderName(name))))
            }

            val Enum = enumName.toTypeName

            val caseObjects = cases.map(cse ⇒
               q"""case object ${cse.name} extends $Enum""")

            val asString = {
               val caseDefs : List[CaseDef] = cases.map(cse ⇒
                  cq"""`${cse.name}` ⇒ ${cse.str}""")

               q"""protected def asStringImpl(e : $Enum) = e match {
                  case ..$caseDefs
               }"""
            }

            val fromString = {
               val caseDefs : List[CaseDef] = cases.map { cse ⇒
                  cq"""`${cse.str}` ⇒ Some(${cse.name})"""
               }

               q"""protected def fromStringImpl(s : String) : Option[$Enum] = s match {
                  case ..$caseDefs
                  case _ ⇒ None
               }"""
            }

            val indexedCases = zipWithIndex(cases)

            val asInt = {
               val caseDefs : List[CaseDef] = indexedCases map {
                  case (i, cse) ⇒ cq"`${cse.name}` ⇒ $i"
               }

               q"""protected def asIntImpl(e : $Enum) = e match {
                  case ..$caseDefs
               }"""
            }

            val fromInt = {
               val caseDefs : List[CaseDef] = indexedCases map {
                  case (i, cse) ⇒ cq"$i ⇒ Some(${cse.name})"
               }

               q"""protected def fromIntImpl(i : Int) : Option[$Enum] = i match {
                  case ..$caseDefs
                  case _ ⇒ None
               }"""
            }

            val first = q"protected def firstImpl : $Enum = ${cases.head.name}"
            val last = q"protected def lastImpl : $Enum = ${cases.last.name}"

            val values = q"""
               protected val valuesImpl : Set[$Enum] = Set(..${cases.map(_.name)})
            """

            val className = q"""
               protected val className : String = ${Enum.decoded.toString}
            """

            val apiImpl = List(
               asString,
               fromString,
               asInt,
               fromInt,
               first,
               last,
               values,
               className)

            val enumApi = tq"macrame.EnumApi[$Enum]"

            val companionObj = companion match {
               case Some(ModuleDef(mods, objName, objImpl)) ⇒
                  val newParents = enumApi :: (objImpl.parents.filter {
                     case Select(Ident(scala), anyRef) if scala.toString == "scala" && anyRef.toString == "AnyRef" ⇒ false
                     case _ ⇒ true
                  })
                  ModuleDef(
                     mods,
                     objName,
                     Template(
                        newParents,
                        objImpl.self,
                        caseObjects ++ apiImpl ++ objImpl.body))
               case None ⇒
                  val newParents = enumApi :: (impl.parents.filter {
                     case Select(Ident(scala), anyRef) if scala.toString == "scala" && anyRef.toString == "AnyRef" ⇒ false
                     case _ ⇒ true
                  })
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
