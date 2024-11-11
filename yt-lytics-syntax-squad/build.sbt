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

libraryDependencies += "com.google.http-client" % "google-http-client-gson" % "1.39.2"
libraryDependencies += "com.typesafe.play" %% "play" % "2.8.8"
libraryDependencies += "javax.inject" % "javax.inject" % "1"
dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.2.0"



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
