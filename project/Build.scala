import sbt._
import sbt.Keys._

object Build extends Build {

   lazy val root: Project = Project(
      "root",
      file("."),
      aggregate = Seq(base, examples),
      settings = commonSettings ++ Seq(
         publishArtifact := false
      )
   )

   lazy val base: Project = Project(
      "macro",
      file("macros"),
      settings = commonSettings ++ Seq(
         version := "0.1-SNAPSHOT",
         libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-compiler" % _)
      )
   )


   lazy val examples: Project = Project(
      "examples",
      file("examples"),
      settings = commonSettings ++ Seq(
         version := "0.1-SNAPSHOT"
      )
   ).dependsOn(base)

   def commonSettings = Defaults.defaultSettings ++
      Seq(
         organization := "macrame"
       , version      := "0.0.1-SNAPSHOT"
       , scalaVersion := "2.11.4"
       , scalacOptions ++= Seq(
            "-unchecked"
          , "-deprecation"
          , "-feature"
          , "-language:higherKinds"
          , "-language:postfixOps"
          )
       )
}
