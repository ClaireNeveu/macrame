# Macramé-Play
Macrame-Play provides `Reads`, `Writes`,`Format`, `QueryStringBindable`, and `PathBindable` instances for Macramé enumerations.

## Getting Macramé-Play
If you're using SBT, add the following to your build file.
```scala
libraryDependencies += "com.claireneveu" %% "macrame-play" % "1.1.0-play-2.5.x"
```

## API Documentation
Full API documentation is available [here](http://claireneveu.github.io/macrame/doc/macrame-play/1.1.0-play-2.5.x/#package).

### Usage
This example shows how to make a `Format` instance for a `Color` enumeration:
```scala
import macrame.play.enums.JsonConverters

@enum class Color {
   Red
   Blue
   Yellow
}
object Color extends JsonConverters[Color]
```
