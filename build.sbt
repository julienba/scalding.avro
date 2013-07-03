name := "avro-source"

organization := "scalding.avro"

version := "0.3.0-SNAPSHOT"

scalaVersion := "2.10.1"

resolvers += "Concurrent Maven Repo" at "http://conjars.org/repo"

resolvers += "Maven Central Repo" at "http://repo1.maven.org/maven2"

resolvers += "Maven Repository" at "http://mvnrepository.com/artifact/"

resolvers += "sonatype releases" at "http://oss.sonatype.org/content/repositories/releases"

//libraryDependencies += "com.twitter" %% "scalding" % "0.8.2"

libraryDependencies ++= Seq(
  "org.apache.thrift" % "libthrift" % "0.6.1",
  "com.twitter" % "scalding-core_2.10" % "0.8.6",
  "com.twitter" % "scalding-args_2.10" % "0.8.6",                                                                                                                                                                   
  "com.twitter" % "scalding-commons_2.10" % "0.2.0" exclude("jvyaml", "jvyaml"),
  "com.twitter" % "scalding-date_2.10" % "0.8.6"
)

libraryDependencies += "cascading.avro" % "avro-scheme" % "2.1.1"

libraryDependencies += "org.apache.hadoop" % "hadoop-core" % "1.0.3"



