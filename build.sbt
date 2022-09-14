import Dependencies.{CompilerPlugins, Libraries}

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "downloader",
    idePackagePrefix := Some("fi.spectrum")
  )
  .settings(
    libraryDependencies ++= List(
      Libraries.derevoPureconfig,
      Libraries.newtype,
      Libraries.tofuDoobie,
      Libraries.tofuZio,
      Libraries.tofuStreams,
      Libraries.tofuFs2,
      Libraries.tofuOpticsInterop,
      Libraries.circeParse,
      Libraries.scalaland,
      Libraries.doobiePg,
      Libraries.doobieHikari,
      Libraries.doobieCore,
      Libraries.pureconfig
    ) ++ List(CompilerPlugins.betterMonadicFor, CompilerPlugins.kindProjector)
  )
