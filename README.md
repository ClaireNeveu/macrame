# Macramé
Macrame provides macro-base replacements for parts of the Scala standard library.

## Getting Macramé
If you're using SBT, add the following to your build file.
```scala
libraryDependencies ++= Seq(
   "com.chrisneveu" %% "macrame" % "1.0.1",
   compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full))
```

## API Documentation
Full API documentation is available [here](http://chrisneveu.github.io/macrame/doc/1.0.0/#package).

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
   case object Red
   case object Blue
   case object Yellow
   <EnumApi implementation elided>
}
```

### Using EnumApi
In order to reduce boilerplate, the `@enum` macro defines a number of convenient functions on your enumeration type. Auto-generated functions are great but they often increase your API in undesired ways, exposing conversions to/from `String` that would be better hidden inside more principled conversions. 

To resolve this tension, `@enum` provides the *implementations* for automatically generated functions as `protected` members of the companion object, leaving you to expose these functions or use them to implement other functions as you wish. These functions can be found in [EnumApi](http://chrisneveu.github.io/macrame/doc/1.0.0/#macrame.EnumApi) (select "Visibility: All").
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
