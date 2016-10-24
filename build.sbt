lazy val root = (project in file(".")).
  aggregate(contrib, mqtt, amqp, `cluster-http`).
  enablePlugins(GitVersioning)

lazy val contrib = project
lazy val mqtt = project
lazy val amqp = project
lazy val `cluster-http` = project

git.useGitDescribe := true
publishArtifact := false
