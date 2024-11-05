name := """yt-lytics-syntax-squad"""
organization := "syntax-squad"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)

scalaVersion := "2.13.15"


libraryDependencies += guice
libraryDependencies += "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0"
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-api" % "5.7.0" % Test
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-engine" % "5.7.0" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "3.9.0" % Test