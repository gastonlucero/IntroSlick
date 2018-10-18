name := "IntroSlick"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.4",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)