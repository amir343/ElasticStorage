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

import sbt._
import Keys._


object EStoreSimBuild extends Build {

  import Dependencies._
  import BuildSettings._

  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  }


  /**
   * eStoreSim project
   */
  lazy val eStoreSim = Project("ess", file("."))
    .settings(basicSettings: _*)
    .aggregate(core, instance)


  /**
   * ess-core module
   */
  lazy val core = Project("ess-core", file("ess-core"))
    .settings(basicSettings: _*)
    .settings(formatSettings: _*)
    .settings(libraryDependencies ++= Compile.allDependencies)


  /**
   * ess-instance module
   */
  lazy val instance = Project("ess-instance", file("ess-instance"))
    .settings(resolversSettings: _*)
    .settings(formatSettings: _*)
    .settings(libraryDependencies ++= Compile.allDependencies)
    .dependsOn(core)

}