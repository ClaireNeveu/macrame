# Macramé-Scalaz
Macrame-Scalaz provides instances of several scalaz type-classes for Macramé enumerations.

## Getting Macramé-Scalaz
If you're using SBT, add the following to your build file.
```scala
libraryDependencies += "com.claireneveu" %% "macrame-scalaz" % "1.0.1-scalaz-7.2.x"
```

## API Documentation
Full API documentation is available [here](http://claireneveu.github.io/macrame/doc/macrame-scalaz/1.0.0-scalaz-7.2.x/#package).

### Usage
This example shows how to make a `Semigroup` instance for a `Mod5` enumeration:
```scala
import macrame.scalaz.enums.IsSemigroup

@enum class Mod5 {
   Zero
   One
   Two
   Three
   Four
   Five
}
object Mod5 extends IsSemigroup[Mod5]
```
