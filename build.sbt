name := "hot-reload"

version := "2.0.0"
organization := "com.github.gekomad"
scalaVersion := "2.13.0-RC1"

libraryDependencies += "com.lambdista" %% "config" % "0.5.5-RC1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0-SNAP10" % Test

//crossScalaVersions := Seq("2.11.12", "2.12.6", "2.12.8")

publishTo := sonatypePublishTo.value
