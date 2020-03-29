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
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test,
  "com.h2database" % "h2" % "1.4.199"
)
