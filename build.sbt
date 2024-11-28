ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.1"

resolvers += Resolver.jcenterRepo

// add libGDX stuff
val gdxVersion = "1.9.3"

lazy val root = (project in file("."))
  .settings(
    name := "game",
    idePackagePrefix := Some("com.benmosheron"),
    libraryDependencies ++=
      Seq(
        "com.badlogicgames.gdx" % "gdx" % gdxVersion withSources(),
        "com.badlogicgames.gdx" % "gdx-backend-lwjgl" % gdxVersion withSources(),
        "com.badlogicgames.gdx" % "gdx-platform" % gdxVersion classifier "natives-desktop",
        "org.typelevel" %% "cats-core" % "2.12.0" withSources(),
//        "org.typelevel" %% "cats-effect" % "3.5.6" withSources(),
        "io.circe" %% "circe-core" % "0.14.10",
        "io.circe" %% "circe-generic" % "0.14.10",
        "io.circe" %% "circe-parser" % "0.14.10"
      )
  )
