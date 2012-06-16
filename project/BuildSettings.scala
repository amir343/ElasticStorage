import sbt._
import Keys._
import com.typesafe.sbtscalariform.ScalariformPlugin
import com.typesafe.sbtscalariform.ScalariformPlugin.ScalariformKeys

/**
 * Copyright 2012 Amir Moulavi (amir.moulavi@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Amir Moulavi
 */

object BuildSettings {
  lazy val basicSettings = Seq[Setting[_]](
    name          := "EStoreSim",
    version       := "1.0",
    homepage      := Some(new URL("https://github.com/amir343/ElasticStorage")),
    organization  := "SICS",
    organizationHomepage := Some(new URL("http://www.sics.se/")),
    description   := "Key-Value Store Simulator",
    startYear     := Some(2012),
    licenses      := Seq("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scalaVersion  := "2.9.1",
    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
    resolvers     := Seq(
                      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                      "SICS Snapshot Repository" at "http://kompics.sics.se/maven/snapshotrepository"
                     )
  )

  lazy val noPublishing = Seq(
    publish := (),
    publishLocal := ()
  )

  lazy val formatSettings = ScalariformPlugin.scalariformSettings ++ Seq(
      ScalariformKeys.preferences in Compile := formattingPreferences,
      ScalariformKeys.preferences in Test    := formattingPreferences
    )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
    .setPreference(RewriteArrowSymbols, true)
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
  }

}