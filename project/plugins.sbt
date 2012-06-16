resolvers ++= Seq(
    Resolver.url("sbt-plugin-releases", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
    "less is" at "http://repo.lessis.me",
    "coda" at "http://repo.codahale.com")

addSbtPlugin("com.typesafe.sbtscalariform" % "sbtscalariform" % "0.4.0")