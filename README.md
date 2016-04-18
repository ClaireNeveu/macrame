# Macramé
Macrame provides macro-based replacements for parts of the Scala standard library.

## Getting Macramé
If you're using SBT, add the following to your build file.
```scala
libraryDependencies ++= Seq(
   "com.chrisneveu" %% "macrame" % "1.2.2",
   compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full))
```

## API Documentation
Full API documentation is available [here](http://chrisneveu.github.io/macrame/doc/macrame/1.2.2/#package).

## Enumerations
Macramé provides an `@enum` macro annotation to replace Scala's `Enumeration` class. Unlike `Enumeration`, `@enum` classes are fully type-safe and provide exhaustiveness checks. `@enum` provides a much larger interface than `Enumeration` but it allows the user to select which functions to expose.

### Getting Started
To understand how to use the `@enum` macro, we'll look at a simple `Color` enumeration and see what it expands to.
```scala
@enum class Color {
   Red
   Blue
   Yellow
}
// Expands to:
sealed abstract class Color extends Product with Serializable
object Color extends EnumApi[Color] {
   case object Red extends Color
   case object Blue extends Color
   case object Yellow extends Color
   <EnumApi implementation elided>
}
```

### Using EnumApi
In order to reduce boilerplate, the `@enum` macro defines a number of convenient functions on your enumeration type. Auto-generated functions are great but they often increase your API in undesired ways, exposing conversions to/from `String` that would be better hidden inside more principled conversions. 

To resolve this tension, `@enum` provides the *implementations* for automatically generated functions as `protected` members of the companion object, leaving you to expose these functions or use them to implement other functions as you wish. These functions can be found in [EnumApi](http://chrisneveu.github.io/macrame/doc/macrame/1.2.2/#macrame.EnumApi) (select "Visibility: All").
```scala
@enum class Color {
   Red
   Blue
   Yellow
}
object Color {
   def asString(color : Color) = asStringImpl(color)
   // Replicating Enumeration's partial String→Enumeration.Value conversion.
   def withName(s : String) = fromStringImpl(s)
      .getOrElse(throw new NoSuchElementException(s"No value found for '$s'"))
}
```

Macramé also provides a number of [traits](http://chrisneveu.github.io/macrame/doc/macrame/1.2.2/#macrame.enums.package) that expose the most commonly used functionality. The [Macramé-Play](https://github.com/ChrisNeveu/macrame/tree/master/macrame-play) and [Macramé-Scalaz](https://github.com/ChrisNeveu/macrame/tree/master/macrame-scalaz) libraries leverage this approach to provide integration with Play Framework and Scalaz respectively.

### Providing Custom String Representations
As with `Enumeration`, you can provide custom `String` representations of your enum cases. These can be either a string literal or an identifier pointing to a string. You can mix-and-match the automatically generated representations with manual ones.
```scala
@enum class Color {
   Red("RED")
   Blue("BLUE")
   Yellow("YELLOW")
}
object Color {
   def asString(color : Color) = asStringImpl(color)
   def fromString(s : String) = fromStringImpl(s)
}
```

## Regular Expressions
Via the `r` string interpolator, Macramé provides compile-time checked regular expressions. Interpolated variables are correctly escaped.
```scala
val separator = "-"
val PhoneNumber = r"""\d{3}$separator\d{4}"""
```

## Creating Delegates
"delegate" or "proxy" classes are a powerful pattern for creating composable data types. Unfortunately programming this way forces you to write a lot of boilerplate, either writing accessors for each member of the wrapped type or adding an extra call (`wrapper.underlying.method`) whenever you need access to the wrapped type. The `@delegate` macro provides an easy solution to this problem allowing you to delegate method calls to a class parameter or member.
```scala
// In file User.scala
case class User(name : String, password : String)
// In file Admin.scala
case class Admin(@delegate underlying : User, privileges : List[Board])
abstract class Admin2 {
  @delegate
  def underlying : User
  def privileges : List[Board]
}
// Expands to:
case class User(name : String, password : String)
case class Admin(underlying : User, privileges : List[Board]) {
  def name : String = underlying.name
  def password : String = underlying.password
}
abstract class Admin2 {
  def underlying : User
  def privileges : List[Board]
  def name : String = underlying.name
  def password : String = underlying.password
}
```

### Limitations
Delegating to a class member currently has a number of limitations. Due to the way macro annotations currently work, it is impossible to detect naming collisions before expansion so these will result in compile errors. Additionally, the annotated member must have an explicit type ascription.

These limitations do not exist when delegating to a class parameter. In that case any colliding names will not be delegated to. For this reason it is preferred that you use parameter delegation wherever possible.

In both situations it is impossible to delegate to a type defined within the same immediate scope the enclosing class. This is unfortunately a limitation of macro annotations. To workaround this defined any types being delegated to in another file.

## Selecting Members
Often the key to good boilerplate-elimination, Macramé provides two functions to select members of objects: `members` and `memberMap`. When used inside the selected object, be sure to use type ascriptions otherwise these functions will try to contain themselves.
```scala
@enum class Color {
   Red
   Blue
   Yellow
}
object Color {
   val values : List[Color] = members[Color](this)
}
```
## Debugging
The `trace` macro can be very useful when figuring out why a macro won't work. It outputs to the console during compiliation. The format looks like this:
```console
[info] /home/chris/Programming/scala/macrame/README.scala:70: trace output
[info]    immutable.this.List.apply[Color](this.Red, this.Blue, this.Yellow)
[info] for position:
[info]       val values : List[Color] = trace(members[Color](this))
[info]                                        ^
```
