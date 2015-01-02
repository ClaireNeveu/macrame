package macrame.examples

object Example extends App {
	object foo {
		val a : String = "first"
		val b : String = "second"
		val c : Int = 3
		val d : Long = 4L
		val e : String = "fifth"
	}
	import macrame.compile._

   lazy val fooMembers = trace(members[String](foo))
   lazy val fooMap = memberMap[String](foo)
   lazy val fooTup = memberProduct[String](a => a)(foo)
	lazy val reg = r"test{"
}
