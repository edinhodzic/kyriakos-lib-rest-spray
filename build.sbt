import sbt._

organization := "io.kyriakos.library"

name := "kyriakos-lib-rest-spray"

version := "0.6.0-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val kyriakosLibRestSpray = project.in(file("."))

libraryDependencies ++= Seq(
  // scala
  "org.scala-lang" % "scala-library" % "2.11.7",
  // spray
  "io.spray" % "spray-can_2.11" % "1.3.3",
  "io.spray" % "spray-json_2.11" % "1.3.2",
  "io.spray" % "spray-routing_2.11" % "1.3.3",
  // akka
  "com.typesafe.akka" % "akka-actor-tests_2.11" % "2.3.9",
  // kamon
  "io.kamon" % "kamon-core_2.11" % "0.5.2",
  // logging
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.typesafe.scala-logging" % "scala-logging_2.11" % "3.1.0",
  // kyriakos
  "io.kyriakos.library" % "kyriakos-lib-utils_2.11" % "0.1.0-SNAPSHOT",
  "io.kyriakos.library" % "kyriakos-lib-domain_2.11" % "0.5.0-SNAPSHOT",
  "io.kyriakos.library" % "kyriakos-lib-repository-h2_2.11" % "0.5.0-SNAPSHOT",
  // test
  "io.spray" % "spray-testkit_2.11" % "1.3.3" % "test",
  "org.specs2" % "specs2-core_2.11" % "3.6.6" % "test",
  "org.specs2" % "specs2-junit_2.11" % "3.6.6" % "test",
  "org.specs2" % "specs2-mock_2.11" % "3.6.6" % "test"
)

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

scalacOptions ++= Seq("-deprecation", "-feature")
