package macrame.internal

import reflect.macros.whitebox.Context

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

