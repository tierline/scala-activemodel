organization := "com.tierline.sails"
name := "activemodel"
version := "1.0.0"
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.clapper" %% "grizzled-slf4j" % "1.0.2",
  "com.github.nscala-time" %% "nscala-time" % "1.4.0",
  "org.squeryl" %% "squeryl" % "0.9.5-7",
  "com.tierline.sails" %% "servicelocator" % "2.1.0",
  //Java libs
  "ch.qos.logback" % "logback-classic" % "1.0.11",
  "com.mchange" % "c3p0" % "0.9.5-pre9",
  "com.typesafe" % "config" % "1.3.0",
  //test libs
  "org.scalatest" %% "scalatest" % "2.2.2" % "test",
  "com.h2database" % "h2" % "1.4.182" % "test"
)