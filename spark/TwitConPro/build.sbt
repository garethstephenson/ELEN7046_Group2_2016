name := "TwitConPro"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.1" % "provided"
libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs" % "2.6.4" % "provided"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"
//libraryDependencies += "net.liftweb" %% "lift-json" % "2.6"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)