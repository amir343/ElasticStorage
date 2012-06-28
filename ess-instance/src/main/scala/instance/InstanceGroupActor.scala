package instance

import akka.actor._
import scala.collection.mutable
import protocol._
import protocol.InstanceGroupStart
import protocol.Stopped
import protocol.InstanceStart
import scheduler.SchedulerActor
import cloud.CloudProviderActor
import cloud.common.NodeConfiguration

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
class InstanceGroupActor extends Actor with ActorLogging {

  private val currentNodes = mutable.ListBuffer[ActorRef]()
  private val NODE_PREFIX = "instance%s"

  def receive = {
    case InstanceGroupStart(conf) ⇒ startInstances(conf)
    case Stopped()                ⇒ handleStop()
  }

  private def startInstances(conf: InstanceGroupConfiguration) {
    log.info("Bringing up %s instances...".format(conf.nodes.size))
    val indices = 1 to conf.nodes.size
    conf.nodes.zip(indices).foreach { pair ⇒
      val node = context.actorOf(Props[InstanceActor])
      node ! InstanceStart(pair._1, NODE_PREFIX.format(pair._2.toString))
      currentNodes += node
    }
  }

  private def handleStop() {
    currentNodes -= sender
    currentNodes.size match {
      case 0            ⇒ context.system.shutdown()
      case instance @ _ ⇒ log.info("Instance %s is stopped".format(instance))
    }
  }

}

object InstanceActorApp {
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