name := "Ping Pong"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies += "com.typesafe.akka" %% "akka-actor"   % "2.3.3"
libraryDependencies += "io.spray" %% "spray-can" % "1.3.3"
