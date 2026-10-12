ThisBuild / scalaVersion := "3.9.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "saillog",
    libraryDependencies ++= Seq(
      "com.lihaoyi"            %% "os-lib"    % "0.11.8",
      "com.lihaoyi"            %% "upickle"   % "4.4.3",
      "org.scala-lang.modules" %% "scala-xml" % "2.5.0"
    ),
    run / fork := true
  )
