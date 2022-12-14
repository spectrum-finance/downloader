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
    scalacOptions ++= commonScalacOption,
    assembly / test := {},
    assembly / assemblyMergeStrategy := {
      case "logback.xml"                                => MergeStrategy.first
      case "module-info.class"                          => MergeStrategy.discard
      case "META-INF/intellij-compat.json"              => MergeStrategy.last
      case other if other.contains("io.netty.versions") => MergeStrategy.first
      case other if other.contains("scala")             => MergeStrategy.first
      case other if other.contains("derevo")            => MergeStrategy.last
      case other                                        => (assembly / assemblyMergeStrategy).value(other)
    }
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
  .settings(assembly / assemblyJarName := "downloader.jar")
  .enablePlugins(JavaAppPackaging, UniversalPlugin, DockerPlugin)
