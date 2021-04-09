scalaVersion := "3.0.0-RC1"

turbo := true
useSuperShell := false
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)
libraryDependencies ++= Seq("org.jsoup"   % "jsoup"  % "1.13.1")