package macrame.internal

import reflect.macros.blackbox.Context

sealed abstract class TreeShaper[C <: Context] private (val c : C) { self ⇒
   import c.universe._
   import TreeShaper.KleisliOps

   def run(tree : Tree) : List[Tree] = transformer(tree)

   val transformer : Tree ⇒ List[Tree]

   def members = new TreeShaper[c.type](c) {
      val transformer = self.transformer >>> TreeShaper.getMembers(c) _
   }

   def members(f : Tree ⇒ List[Tree]) = new TreeShaper[c.type](c) {
      val transformer = self.transformer >>> TreeShaper.procMembers(c)(f) _
   }

   def modifyRec(f : Tree ⇒ List[Tree]) = new TreeShaper[c.type](c) {
      val transformer = self.transformer >>> TreeShaper.procAll(c)(f) _
   }

   def filter(p : Tree ⇒ Boolean) = new TreeShaper[c.type](c) {
      val transformer = self.transformer >>> ((t : Tree) ⇒
         if (p(t)) List(t) else Nil)
   }

   def flatMap(p : Tree ⇒ List[Tree]) = new TreeShaper[c.type](c) {
      val transformer = self.transformer >>> p
   }
}

object TreeShaper {
   def apply[C <: Context](c : C) = new TreeShaper[c.type](c) {
      val transformer = (t : c.Tree) ⇒ List(t)
   }
   implicit class KleisliOps[A](f : A ⇒ List[A]) {
      def >>>(g : A ⇒ List[A]) : A ⇒ List[A] =
         (a : A) ⇒ f(a).flatMap(g)
   }
   def getMembers(c : Context)(tree : c.Tree) : List[c.Tree] = {
      import c.universe._
      tree match {
         case ClassDef(mods, name, tparams, impl) ⇒ impl.body
         case ModuleDef(mods, name, impl)         ⇒ impl.body
         case _                                   ⇒ Nil
      }
   }
   def procMembers(c : Context)(f : c.Tree ⇒ List[c.Tree])(tree : c.Tree) : List[c.Tree] = {
      import c.universe._
      tree match {
         case ClassDef(mods, name, tparams, impl) ⇒
            val Template(parents, self, body) = impl
            val newImpl = Template(parents, self, body.flatMap(f))
            List(ClassDef(mods, name, tparams, newImpl))
         case ModuleDef(mods, name, impl) ⇒
            val Template(parents, self, body) = impl
            val newImpl = Template(parents, self, body.flatMap(f))
            List(ModuleDef(mods, name, newImpl))
         case t ⇒ List(t)
      }
   }
   def procAll(c : Context)(f : c.Tree ⇒ List[c.Tree])(tree : c.Tree) : List[c.Tree] =
      f(tree).flatMap(procMembers(c)(procAll(c)(f)(_))(_))
}

object enum {
   def impl(c : Context)(annottees : c.Expr[Any]*) : c.Expr[Any] = {
      import c.universe._
      import Flag._
      val (input : Tree, companion : Option[Tree]) = annottees.map(_.tree) match {
         case ClassDef(mods, enumName, tparams, impl) :: obj :: Nil ⇒
            (ClassDef(mods, enumName, tparams, impl), Some(obj))
         case ClassDef(mods, enumName, tparams, impl) :: Nil ⇒
            (ClassDef(mods, enumName, tparams, impl), None)
         case _ ⇒ c.abort(NoPosition, "Enum must be a class.")
      }
      TreeShaper[c.type](c).members {
         case t @ Ident(_) ⇒ List(t)
         case t @ DefDef(_, name, _, _, _, _) if name.decoded == "<init>" ⇒ List(t)
         case t ⇒
            c.abort(t.pos, "An enum may only contain identifiers.")
            List(t)
      } run input
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
            val cases = impl.body.collect {
               case Ident(name) ⇒
                  val enumType = enumName.toTypeName
                  q"""case object ${name.toTermName} extends $enumType"""
            }
            val companionObj = companion match {
               case Some(ModuleDef(mods, objName, objImpl)) ⇒ ModuleDef(
                  mods,
                  objName,
                  Template(objImpl.parents, objImpl.self, cases ++ objImpl.body))
               case None ⇒ ModuleDef(
                  Modifiers(),
                  enumName.toTermName,
                  Template(impl.parents, impl.self, init :: cases))
            }
            List(
               q"sealed abstract class ${enumName.toTypeName} extends Product with Serializable",
               companionObj)
         case _ ⇒ c.abort(NoPosition, "Enum must be a class.")
      }
      println(outputs)
      c.Expr[Any](Block(outputs, Literal(Constant(()))))
   }
}

