name := "CameraControlLibrary"

version := "1.0"

scalaVersion := "2.11.0"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "org.scalafx" %% "scalafx" % "1.0.0-R8",
  "org.spire-math" %% "spire" % "0.8.2",
  "com.squants"  %% "squants"  % "0.4.2",
  "org.mongodb" %% "casbah" % "2.7.2"
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"