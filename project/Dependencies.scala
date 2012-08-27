import sbt._

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

object Dependencies {

  object Compile {

    val akkaActor =        "com.typesafe.akka"       %      "akka-actor"         %        "2.0"
    val akkaTestKit =      "com.typesafe.akka"       %      "akka-testkit"       %        "2.0"

    // Deprecated
/*
    val kompicsCore = "se.sics.kompics" % "kompics-core" % "0.4.3-SNAPSHOT"
    val kompicsTimer = "se.sics.kompics.basic" % "kompics-component-java-timer" % "0.4.3-SNAPSHOT"
    val kompicsMinaNetwork = "se.sics.kompics.basic" % "kompics-component-mina-network" % "0.4.3-SNAPSHOT"
    val kompicsPLauncher = "se.sics.kompics.launcher" % "kompics-local-process-launcher" % "0.4.3-SNAPSHOT"
*/

    val googleCollections = "com.google.collections" % "google-collections" % "1.0-rc4"
    val commonsCollection = "commons-collections" % "commons-collections" % "3.2.1"
    val commonsLang = "commons-lang" % "commons-lang" % "2.2"
    val commonsIO = "commons-io" % "commons-io" % "2.0"
    val commonsMath = "org.apache.commons" % "commons-math" % "2.0"
    val xstream = "com.thoughtworks.xstream" % "xstream" % "1.2.2"
    val trident = "org.pushing-pixels" % "trident" % "1.2"
    val steelSeries = "eu.hansolo" % "SteelSeries" % "3.9"
    val jfxtras = "org.jfxtras" % "jfxtras-labs" % "0.1"
    val jFreeChart = "jfree" % "jfreechart" % "1.0.9"
    val scalaTest = "org.scalatest" %% "scalatest" % "1.8"

    val allDependencies = Seq(
      akkaActor,
      akkaTestKit,
/*
      kompicsCore,
      kompicsTimer,
      kompicsMinaNetwork,
      kompicsPLauncher,
*/
      googleCollections,
      commonsCollection,
      commonsLang,
      commonsIO,
      commonsMath,
      xstream,
      trident,
      steelSeries,
      jfxtras,
      jFreeChart,
      scalaTest
    )

  }

}
