package macrame.scalaz.enums

import macrame.enum

import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

import org.scalacheck.Gen

class EnumTest extends FunSuite with PropertyChecks {
   test("Equal instance should obey laws.") {
      @enum class Color {
         Red
         Blue("BLUE")
         Yellow("yellow")
      }
      object Color extends IsEqual[Color] with HasGenerator[Color]
      forAll { (a : Color, b : Color, c : Color) ⇒
         assert(Color.equal.equalLaw.commutative(a, b))
         assert(Color.equal.equalLaw.naturality(a, b))
         assert(Color.equal.equalLaw.reflexive(a))
         assert(Color.equal.equalLaw.transitive(a, b, c))
      }
   }

   test("Order instance should obey laws.") {
      @enum class Mod5 {
         Zero
         One
         Two
         Three
         Four
         Five
      }
      object Mod5 extends IsOrder[Mod5] with HasGenerator[Mod5]
      forAll { (a : Mod5, b : Mod5, c : Mod5) ⇒
         assert(Mod5.order.orderLaw.commutative(a, b))
         assert(Mod5.order.orderLaw.naturality(a, b))
         assert(Mod5.order.orderLaw.reflexive(a))
         assert(Mod5.order.orderLaw.transitive(a, b, c))
         assert(Mod5.order.orderLaw.orderAndEqualConsistent(a, b))
         assert(Mod5.order.orderLaw.transitiveOrder(a, b, c))
      }
   }

   test("Enum instance should obey laws.") {
      @enum class Mod5 {
         Zero
         One
         Two
         Three
         Four
         Five
      }
      object Mod5 extends IsEnum[Mod5] with HasGenerator[Mod5]
      assert(Mod5.enum.enumLaw.minmaxpred)
      assert(Mod5.enum.enumLaw.minmaxsucc)
      forAll { (a : Mod5) ⇒
         assert(Mod5.enum.enumLaw.reflexive(a))
         assert(Mod5.enum.enumLaw.succorder(a))
         assert(Mod5.enum.enumLaw.succpred(a))
      }
      forAll { (a : Mod5, b : Mod5) ⇒
         assert(Mod5.enum.enumLaw.commutative(a, b))
         assert(Mod5.enum.enumLaw.naturality(a, b))
         assert(Mod5.enum.enumLaw.orderAndEqualConsistent(a, b))
      }
      forAll { (a : Mod5, b : Mod5, c : Mod5) ⇒
         assert(Mod5.enum.enumLaw.transitive(a, b, c))
         assert(Mod5.enum.enumLaw.transitiveOrder(a, b, c))
      }
      forAll(Mod5.gen, Gen.choose(-100, 100)) { (a : Mod5, n : Int) ⇒
         assert(Mod5.enum.enumLaw.predn(a, n))
         assert(Mod5.enum.enumLaw.succn(a, n))
      }
   }

   test("Semigroup instance should obey laws.") {
      @enum class Mod5 {
         Zero
         One
         Two
         Three
         Four
         Five
      }
      object Mod5 extends IsSemigroup[Mod5] with IsEqual[Mod5] with HasGenerator[Mod5]
      forAll { (a : Mod5, b : Mod5, c : Mod5) ⇒
         assert(Mod5.semigroup.semigroupLaw.associative(a, b, c))
      }
   }

   test("Monoid instance should obey laws.") {
      @enum class Mod5 {
         Zero
         One
         Two
         Three
         Four
         Five
      }
      object Mod5 extends IsMonoid[Mod5] with IsEqual[Mod5] with HasGenerator[Mod5]
      forAll { (a : Mod5, b : Mod5, c : Mod5) ⇒
         assert(Mod5.monoid.monoidLaw.associative(a, b, c))
         assert(Mod5.monoid.monoidLaw.leftIdentity(a))
         assert(Mod5.monoid.monoidLaw.rightIdentity(a))
      }
   }
}
