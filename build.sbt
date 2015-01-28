import NativePackagerKeys._

name := "faz-realtime"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions in ThisBuild ++= Seq("-feature", "-language:postfixOps", "-language:implicitConversions", "-deprecation")

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)
