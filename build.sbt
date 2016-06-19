import sbt._
import bintray.Keys._

organization := "io.kyriakos.library"

name := "kyriakos-lib-rest-spray"

version := "1.0.0"

scalaVersion := "2.11.7"

lazy val kyriakosLibRestSpray = project.in(file(".")).
  settings(bintrayPublishSettings: _*).
  settings(
    sbtPlugin := false,
    name := "kyriakos-lib-rest-spray",
    licenses += ("MIT", url("https://opensource.org/licenses/MIT")),
    publishMavenStyle := false,
    repository in bintray := "kyriakos",
    bintrayOrganization in bintray := None
  )

resolvers += Resolver.url("edinhodzic", url("http://dl.bintray.com/edinhodzic/kyriakos"))(Resolver.ivyStylePatterns)

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
  "io.kyriakos.library" % "kyriakos-lib-utils_2.11" % "1.0.0",
  "io.kyriakos.library" % "kyriakos-lib-domain_2.11" % "1.0.0",
  "io.kyriakos.library" % "kyriakos-lib-repository-h2_2.11" % "1.0.0",
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
