package macrame

import org.scalatest.FunSuite

class RegexTest extends FunSuite {

   test("Invalid regexes should not compile.") {
      assertDoesNotCompile("""r"foo)?"""")
   }

   test("Valid regexes should compile.") {
      val PhoneNumber = r"""(?:\d{3}-)?\d{3}-\d{4}"""
      "555-5555" match {
         case PhoneNumber() ⇒ assert(true)
         case _             ⇒ fail()
      }
   }

   test("Interpolated variables are correctly escaped.") {
      val userName = "^(Regexes)[Rule]$"
      val Regex = r"""This is the name ($userName) as it appears."""
      "This is the name ^(Regexes)[Rule]$ as it appears." match {
         case Regex(name) ⇒ assert(name == userName)
         case _           ⇒ fail()
      }
   }
}
