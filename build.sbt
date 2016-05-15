name := "sun"

organization := "com.peterpotts"

version := "1.0.0-SNAPSHOT"

homepage := Some(url("https://github.com/peterpotts/mobius"))

startYear := Some(2015)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/peterpotts/sun"),
    "scm:git:https://github.com/peterpotts/sun.git",
    Some("scm:git:git@github.com:peterpotts/sun.git")))

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:postfixOps",
  "-Xlint:_",
  "-Xverify",
  "-Yclosure-elim")

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

libraryDependencies ++= {
  object Versions {
    val scalaModules = "1.0.4"
    val scalaTest = "2.2.4"
    val mockito = "1.10.19"
    val akka = "2.3.11"
    val spray = "1.3.3"
    val jodaConvert = "1.8"
    val jodaTime = "2.9.3"
    val typesafeConfig = "1.2.1"
    val slf4j = "1.7.21"
    val scalaLogging = "3.4.0"
    val logback = "1.1.3"
  }

  object Dependencies {
    val scalaCompiler = "org.scala-lang" % "scala-compiler" % scalaVersion.value
    val scalaReflect = "org.scala-lang" % "scala-reflect" % scalaVersion.value
    val scalaParserCombinators = "org.scala-lang.modules" %% "scala-parser-combinators" % Versions.scalaModules
    val scalaXml = "org.scala-lang.modules" %% "scala-xml" % Versions.scalaModules
    val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalaTest

    val mockitoCore = "org.mockito" % "mockito-core" % Versions.mockito

    val akkaActor = "com.typesafe.akka" %% "akka-actor" % Versions.akka
    val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % Versions.akka
    val akkaAgent = "com.typesafe.akka" %% "akka-agent" % Versions.akka
    val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % Versions.akka

    val sprayClient = "io.spray" %% "spray-client" % Versions.spray

    val jodaConvert = "org.joda" % "joda-convert" % Versions.jodaConvert
    val jodaTime = "joda-time" % "joda-time" % Versions.jodaTime

    val typesafeConfig = "com.typesafe" % "config" % Versions.typesafeConfig

    val slf4jApi = "org.slf4j" % "slf4j-api" % Versions.slf4j
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging
    // log4jOverSlf4j: Associated with exclude("log4j", "log4j")
    val log4jOverSlf4j = "org.slf4j" % "log4j-over-slf4j" % Versions.slf4j
    // jclOverSlf4j: Associated with exclude("commons-logging", "commons-logging")
    val jclOverSlf4j = "org.slf4j" % "jcl-over-slf4j" % Versions.slf4j
    val logbackClassic = "ch.qos.logback" % "logback-classic" % Versions.logback
  }

  import Dependencies._

  Seq(
    scalaCompiler,
    scalaReflect,
    scalaParserCombinators,
    scalaXml,
    scalaTest % "test",
    mockitoCore % "test",
    akkaActor,
    akkaSlf4j,
    akkaAgent,
    akkaTestkit % "test",
    sprayClient,
    jodaConvert,
    jodaTime,
    typesafeConfig,
    slf4jApi,
    scalaLogging,
    log4jOverSlf4j,
    jclOverSlf4j,
    logbackClassic)
}

/*-----------------*/
/* Assembly plugin */
/*-----------------*/

mainClass in assembly := Some("com.peterpotts.sun.Application")

assemblyMergeStrategy in assembly := {
  case "reference.conf" => MergeStrategy.first
  case pathList => (assemblyMergeStrategy in assembly).value(pathList)
}
