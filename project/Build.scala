import sbt._
import sbt.Keys._

// Scalariform
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform._
import ScalariformKeys._

import com.typesafe.sbt.pgp.PgpSettings.useGpg

object Build extends Build {

   lazy val root: Project = Project(
      "macrame",
      file("."),
      settings = commonSettings ++ Seq(
         libraryDependencies ++= Seq(
            "org.scala-lang" % "scala-compiler" % scalaVersion.value,
            compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),
            "org.scalatest" %% "scalatest" % "2.2.4" % "test")
      )
   )

   lazy val pomStuff = {
     <url>https://github.com/ChrisNeveu/macrame</url>
     <licenses>
       <license>
         <name>BSD 3-Clause</name>
         <url>https://raw.githubusercontent.com/ChrisNeveu/macrame/master/LICENSE</url>
       </license>
     </licenses>
     <scm>
       <connection>scm:git:github.com/ChrisNeveu/macrame.git</connection>
       <developerConnection>scm:git:git@github.com:ChrisNeveu/macrame.git</developerConnection>
       <url>git@github.com:ChrisNeveu/macrame</url>
     </scm>
     <developers>
       <developer>
         <name>Chris Neveu</name>
         <url>chrisneveu.com</url>
       </developer>
     </developers>
   }

   lazy val commonSettings = Defaults.defaultSettings ++ scalariformSettings ++Seq(
      organization := "com.chrisneveu",
      version      := "1.1.0-SNAPSHOT",
      scalaVersion := "2.11.6",
      scalacOptions ++= Seq(
         "-unchecked",
         "-deprecation",
         "-feature",
         "-language:higherKinds",
         "-language:postfixOps"
      ),
      useGpg := true,
      pomExtra := pomStuff,
      ScalariformKeys.preferences := ScalariformKeys.preferences.value
         .setPreference(IndentSpaces, 3)
         .setPreference(SpaceBeforeColon, true)
         .setPreference(PreserveDanglingCloseParenthesis, true)
         .setPreference(RewriteArrowSymbols, true)
         .setPreference(DoubleIndentClassDeclaration, true)
         .setPreference(AlignParameters, true)
         .setPreference(AlignSingleLineCaseStatements, true)
   )
}
