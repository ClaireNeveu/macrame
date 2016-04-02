package macrame

import org.scalatest.FunSuite

class MembersTest extends FunSuite {
   object Foo {
      val a = "foo"
      val b = 1
      val c = 1L
      val d = 2
      val e = true
      val f : Int = 3
      // private val g = 4
      val h = 4
   }

   test("members correctly lists all members.") {
      assert(members[Int](Foo).sorted == List(1, 2, 3, 4))
   }

   test("memberMap correctly lists all members.") {
      assert(memberMap[Int](Foo) == Map("b" -> 1, "d" -> 2, "f" -> 3, "h" -> 4))
   }
}
