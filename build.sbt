name := "soc-hack"
version := "0.1"
scalaVersion := "2.12.8"

lazy val akkaHttpVersion = "10.1.7"
lazy val akkaVersion    = "2.5.21"

lazy val root = (project in file("."))
  .settings(
    mainClass in (Compile, run) := Some("com.fablerq.dd.Server"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor"           % akkaVersion,
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "com.typesafe.slick" %% "slick" % "3.2.0",
      "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
      "ch.megard" %% "akka-http-cors" % "0.4.0",
      "org.json4s" %% "json4s-jackson" % "3.6.5",
      "net.liftweb" %% "lift-json" % "3.3.0",

      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test
    )
  )

enablePlugins(JavaAppPackaging)




