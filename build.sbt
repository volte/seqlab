name := "SequenceLab"
version := "0.1"
scalaVersion := "2.13.3"

val AkkaVersion = "2.6.10"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.0"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.5"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.5"
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test
