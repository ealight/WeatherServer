name := "WeatherServer"

version := "0.1"

scalaVersion := "2.12.12"

val akkaVersion = "2.6.14"

enablePlugins(JavaAppPackaging)
enablePlugins(AkkaGrpcPlugin)

inConfig(Compile)(Seq(
  PB.protoSources += baseDirectory.value /  "protobufs"
))

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "net.debasishg" %% "redisclient" % "3.30"
)
