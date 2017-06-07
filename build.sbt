organization := "com.tierline"
name := "activemodel"
version := "1.0.0"
scalaVersion := "2.12.2"
parallelExecution in Test := false

libraryDependencies ++= Seq(
  "org.clapper" %% "grizzled-slf4j" % "1.3.1",
  "com.github.nscala-time" %% "nscala-time" % "2.16.0",
  "org.squeryl" %% "squeryl" % "0.9.5-7",
  //Java libs
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "commons-dbcp" % "commons-dbcp" % "1.4",
  "com.mchange" % "c3p0" % "0.9.5.2",
  "com.typesafe" % "config" % "1.3.1",
  //test libs
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.h2database" % "h2" % "1.4.195" % "test",
  "mysql" % "mysql-connector-java" % "5.1.16"
)