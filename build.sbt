name := "hot-reload"

version := "0.3.0"
organization := "com.github.gekomad"
scalaVersion := "2.12.8"

libraryDependencies += "com.lambdista" %% "config" % "0.5.4"

libraryDependencies += "org.scalatest" %% "scalatest"  % "3.0.5"  % Test

crossScalaVersions := Seq("2.12.6", "2.12.8")

publishTo := sonatypePublishTo.value
