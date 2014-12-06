name                  := "akka-cereal"

organization          := "io.neilord"

version               := "0.1.0-SNAPSHOT"

scalaVersion          := "2.11.4"

scalacOptions         ++= Seq("-deprecation", "-feature", "-encoding", "utf8")

val akkaVersion = "2.3.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "net.ceedubs" %% "ficus" % "1.1.1",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.mockito" % "mockito-core" % "1.10.8",
  "org.scream3r" % "jssc" % "2.8.0"
)
