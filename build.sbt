name := "hot-reload"

version := "1.1.0"
organization := "com.github.gekomad"
scalaVersion := "2.13.0"

libraryDependencies += "com.lambdista" %% "config" % "0.6.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test

crossScalaVersions := Seq("2.11.12", "2.12.6", "2.12.8", "2.13.0")

publishTo := sonatypePublishTo.value
