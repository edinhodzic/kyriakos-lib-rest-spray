import sbt._

organization := "io.otrl.library"

name := "otrl-lib-rest-spray"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val otrlLibraryRestSpray = project.in(file("."))

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.11.7",
  "io.spray" % "spray-can_2.11" % "1.3.3",
  "io.spray" % "spray-routing_2.11" % "1.3.3",
  "com.typesafe.akka" % "akka-actor-tests_2.11" % "2.3.9",
  "io.otrl.library" %% "otrl-lib-domain" % "0.1.0-SNAPSHOT",
  "io.otrl.library" %% "otrl-lib-repository" % "0.1.0-SNAPSHOT", // TODO is this necessary given the *-h2 dependency?
  "io.otrl.library" %% "otrl-lib-repository-h2" % "0.1.0-SNAPSHOT"
)

mainClass in Compile := Some("io.otrl.library.rest.spray.CustomerRestService")
