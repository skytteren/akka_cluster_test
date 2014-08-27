import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

name := "akka cluster test seed"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.3.4"

scalaVersion := "2.11.2"

maintainer in Docker := "SK <sk@penger.no>"

packageArchetype.java_application
