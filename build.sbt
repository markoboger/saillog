ThisBuild / scalaVersion := "3.9.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "saillog",
    libraryDependencies ++= Seq(
      "com.lihaoyi"            %% "os-lib"    % "0.11.8",
      "com.lihaoyi"            %% "upickle"   % "4.4.3",
      "org.scala-lang.modules" %% "scala-xml" % "2.5.0",
      "com.lihaoyi"            %% "scalasql"  % "0.3.2",
      "org.xerial"             %  "sqlite-jdbc" % "3.53.4.0",
      "com.lihaoyi"            %% "cask"      % "0.11.3",
      "com.lihaoyi"            %% "scalatags" % "0.13.1"
    ),
    run / fork := true
  )
