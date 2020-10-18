name := "SequenceLab"
version := "0.1"
scalaVersion := "2.13.3"

val AkkaVersion = "2.6.10"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"