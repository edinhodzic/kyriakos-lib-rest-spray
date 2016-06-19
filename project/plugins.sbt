logLevel := Level.Warn

resolvers += Resolver.sonatypeRepo("public")

resolvers += Resolver.url("edinhodzic", url("http://dl.bintray.com/edinhodzic/kyriakos"))(Resolver.ivyStylePatterns)

addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.2.1")
