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
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.8"
libraryDependencies += "org.mockito" % "mockito-inline" % "5.2.0" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.14" // Replace with the latest compatible version
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.14" // For WebSocket flows
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.14" % Test // Optional, for testing Akka actors




enablePlugins(JacocoPlugin)

jacocoIncludes := Seq("controllers.SearchController", "controllers.YouTubeSearch","models.*")

jacocoReportSettings := JacocoReportSettings()
  .withThresholds(
    JacocoThresholds(
      instruction = 50,
      method = 50,
      branch = 50,
      complexity = 50,
      line = 50,
      clazz = 50)
  )
