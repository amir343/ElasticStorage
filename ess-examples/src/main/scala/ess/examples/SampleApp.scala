package ess.examples

import akka.actor.{ Props, ActorSystem }
import protocol.InstanceGroupStart
import scheduler.SchedulerActor
import cloud.CloudProviderActor
import cloud.common.NodeConfiguration
import instance.{ InstanceGroupActor, InstanceGroupConfiguration }

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

object SampleApp {
  def main(args: Array[String]) {
    val system = ActorSystem("testsystem")
    val scheduler = system.actorOf(Props[SchedulerActor], "scheduler")
    val cloudProvider = system.actorOf(Props[CloudProviderActor], "cloudProvider")
    val instanceGroup = system.actorOf(Props[InstanceGroupActor], "instanceGroup")
    val nodeConfig = new NodeConfiguration(2.0, 5.0, 4, 20)
    nodeConfig.setHeadLess(false)
    instanceGroup ! InstanceGroupStart(InstanceGroupConfiguration(List(nodeConfig)))
  }
}