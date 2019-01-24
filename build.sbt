name := "hot-reload"

version := "0.1.0"
organization := "com.github.gekomad"
scalaVersion := "2.12.8"

libraryDependencies += "com.lambdista" %% "config" % "0.5.1"

libraryDependencies += "org.scalatest" %% "scalatest"  % "3.0.5"  % Test

crossScalaVersions := Seq("2.11.12", "2.12.6", "2.12.8")

publishTo := sonatypePublishTo.value
