name := "Web RPG"

version := "1.0"

organization := "com.dylan"

libraryDependencies += "org.scalatest" % "scalatest_2.9.0" % "1.6.1"

scalaVersion := "2.9.0"

seq(webSettings: _*)

resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"

resolvers += "Web plugin repo2" at "http://mpeltonen.github.com/maven/"

libraryDependencies ++= {
  val liftVersion = "2.4-M1"
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-wizard" % liftVersion % "compile->default"
  )
} 

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.5" % "test->default",
  "org.mortbay.jetty" % "jetty" % "6.1.22" % "jetty,test",
  "org.scala-tools.testing" % "specs_2.9.0" % "1.6.8" % "test",
  "javax.servlet" % "servlet-api" % "2.5" % "provided->default",
  "com.h2database" % "h2" % "1.2.138",
  "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default"
)