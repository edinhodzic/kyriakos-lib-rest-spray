logLevel := Level.Warn

resolvers += "OTRL Maven Repo" at "https://mvn.otrl.io"

resolvers += Resolver.sonatypeRepo("public")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

