lazy val MetaVersion     = "1.8.0"
lazy val ParadiseVersion = "3.0.0-M9"
lazy val scala211        = "2.11.11"
lazy val scalameta       = "org.scalameta" %% "scalameta" % MetaVersion
lazy val contrib         = "org.scalameta" %% "contrib" % MetaVersion
lazy val testkit         = "org.scalameta" %% "testkit" % MetaVersion
lazy val paradise        = "org.scalameta" % "paradise" % ParadiseVersion cross CrossVersion.full

lazy val allSettings = Seq(
  organization := "org.scalameta",
  scalaVersion := scala211,
  resolvers += Resolver.bintrayRepo("scalameta", "maven"),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % Test
  ),
  updateOptions := updateOptions.value.withCachedResolution(true)
)

allSettings

name := "scalameta-tutorial"

lazy val library = project.settings(
  allSettings,
  libraryDependencies += scalameta
)

lazy val macros = project.settings(
  allSettings,
  macroSettings,
  // only needed for @generic demo.
  libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.2",
  libraryDependencies += testkit       % Test
)

lazy val readme = scalatex
  .ScalatexReadme(projectId = "readme",
                  wd = file(""),
                  url = "https://github.com/scalameta/tutorial/tree/master",
                  source = "Readme")
  .settings(
    allSettings,
    buildInfoSettings,
    siteSourceDirectory := target.value / "scalatex",
    test := run.in(Compile).toTask(" --validate-links").value,
    libraryDependencies += contrib,
    publish := {
      ghpagesPushSite
        .dependsOn(run.in(Compile).toTask(""))
        .value
    },
    git.remoteRepo := "git@github.com:scalameta/tutorial.git",
    libraryDependencies ++= Seq(
      "com.twitter" %% "util-eval" % "6.34.0",
      "org.pegdown" % "pegdown"    % "1.6.0"
    )
  )
  .enablePlugins(
    GhpagesPlugin,
    BuildInfoPlugin
  )

// Macro setting is any module that has macros, or manipulates meta trees
lazy val macroSettings = Seq(
  libraryDependencies += scalameta,
  addCompilerPlugin(paradise),
  scalacOptions += "-Xplugin-require:macroparadise"
)

lazy val buildInfoSettings: Seq[Def.Setting[_]] = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    version,
    "scalameta" -> MetaVersion,
    "paradise"  -> ParadiseVersion,
    scalaVersion,
    sbtVersion
  ),
  buildInfoPackage := "scala.meta.tutorial"
)
