name := "hivetest"

version := "0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  watchSources ++= (baseDirectory.value / "public/ui" ** "*").get
)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  guice,
  evolutions,
  jdbc,
  "mysql" % "mysql-connector-java" % "5.1.30",
  "com.typesafe.play" %% "anorm" % "2.5.3",
  specs2 % Test,
  "org.mockito" % "mockito-core" % "2.7.22" % Test
)
