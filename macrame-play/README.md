# Macramé-Play
Macrame-Play provides `Reads`, `Writes`, and `Format` instances for Macramé enumerations.

## Getting Macramé-Play
If you're using SBT, add the following to your build file.
```scala
libraryDependencies += "com.chrisneveu" %% "macrame-play" % "1.0.0-play-2.5.x"
```

## API Documentation
Full API documentation is available [here](http://chrisneveu.github.io/macrame/macrame-play/doc/1.0.0-play-2.5.x/#package).

### Usage
This example shows how to make a `Format` instance for a `Color` enumeration:
```scala
import macrame.play.JsonConverters

@enum class Color {
   Red
   Blue
   Yellow
}
object Color extends JsonConverters[Color]
```
