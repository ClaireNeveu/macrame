package macrame

sealed abstract class Ref[A]
final case class Id[A](val key : Long) extends Ref[A]
final case class Resolved[A](val a : A) extends Ref[A]

object Imports {
   type Post = String
   type User = Int

   val somePost : Post = ""
   val someUser : User = 0
}
import Imports._

/*
@ref class RefClassTest(
   type Parent <: Ref,
   type Author <: Ref,
   id : Id[Post],
   parent : Parent[Post],
   author : Author[User])
 */
abstract class RefClassTest {
   type Parent[_] <: Ref[_]
   type Author[_] <: Ref[_]
   val id : Id[Post]
   val parent : Parent[Post]
   val author : Author[User]
}
object RefClassTest {
   def apply[X[_] <: Ref[_], Y[_] <: Ref[_]](
      id : Id[Post],
      parent : X[Post],
      author : Y[User]) : RefClassTest { type Parent[_] = X[_]; type Author[_] = Y[_] } = {

      val id0 = id
      val parent0 = parent
      val author0 = author

      new RefClassTest {
         type Parent[_] = X[_]
         type Author[_] = Y[_]
         val id : Id[Post] = id0
         val parent : X[Post] = parent0
         val author : Y[User] = author0
      }
   }
}

object test {
   val post1 : RefClassTest = RefClassTest(
      id = Id(5),
      parent = Resolved(somePost),
      author = Resolved(someUser))

   val post2 : RefClassTest {
      type Parent[_] = Resolved[_]
      type Author[_] = Resolved[_]
   } = RefClassTest[Resolved, Resolved](
      id = Id(5),
      parent = Resolved(somePost),
      author = Resolved(someUser))
}
