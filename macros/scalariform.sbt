import scalariform.formatter.preferences._
import ScalariformKeys._

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
   .setPreference(IndentSpaces, 3)
   .setPreference(SpaceBeforeColon, true)
   .setPreference(PreserveDanglingCloseParenthesis, true)
   .setPreference(RewriteArrowSymbols, true)
   .setPreference(DoubleIndentClassDeclaration, true)
   .setPreference(AlignParameters, true)
   .setPreference(AlignSingleLineCaseStatements, true)

