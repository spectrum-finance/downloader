import Dependencies.{CompilerPlugins, Libraries}

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val commonScalacOption = List(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Ymacro-annotations"
)

lazy val root = (project in file("."))
  .settings(
    name := "downloader",
    idePackagePrefix := Some("fi.spectrum"),
    scalacOptions ++= commonScalacOption
  )
  .settings(
    libraryDependencies ++= List(
      Libraries.derevoPureconfig,
      Libraries.derevoCirce,
      Libraries.newtype,
      Libraries.tofuDoobie,
      Libraries.tofuZio,
      Libraries.tofuStreams,
      Libraries.tofuFs2,
      Libraries.tofuOpticsInterop,
      Libraries.tofuDerivation,
      Libraries.tofuLogging,
      Libraries.circeParse,
      Libraries.scalaland,
      Libraries.doobiePg,
      Libraries.doobieHikari,
      Libraries.doobieCore,
      Libraries.pureconfig
    ) ++ List(CompilerPlugins.betterMonadicFor, CompilerPlugins.kindProjector)
  )
