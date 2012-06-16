package instance

import akka.actor._
import gui.InstanceGUI
import cloud.common.NodeConfiguration
import protocol._
import akka.util.duration._
import protocol.CPUInit
import protocol.CPULoadDiagram
import protocol.CPULoad
import protocol.UpdateCPUInfoLabel
import protocol.InstanceStart
import protocol.CPUReady
import protocol.CPULog

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
class InstanceActor extends Actor with ActorLogging {

  // Children
  val cpu = context.actorOf(Props[CPUActor])
  val disk = context.actorOf(Props[DiskActor])

  val gui = InstanceGUI.getInstance()
  gui.setInstanceReference(this)

  def receive = genericHandler   orElse
                cpuHandler       orElse
                diskHandler

  def genericHandler:Receive = {
    case InstanceStart(nodeConfig)                         => initialize(nodeConfig)
  }

  def cpuHandler:Receive = {
    case CPUReady()                                        => log.info("CPU is ready")
    case CPULog(msg)                                       => gui.log(msg)
    case UpdateCPUInfoLabel(label)                         => gui.updateCPUInfoLabel(label)
    case CPULoadDiagram(chart)                             => gui.createCPULoadDiagram(chart)
    case CPULoad(load)                                     => gui.cpuLoad(load)
  }

  def diskHandler:Receive = {
    case DiskReady()                                       => log.info("Disk is ready")
    case BlockResponse(block, process)                     => //TODO
  }

  private def initialize(nodeConfig:NodeConfiguration) {
    cpu ! CPUInit(nodeConfig)
    disk ! DiskInit(nodeConfig)
  }

  def stopActor() {
    log.info("Getting ready to die!")
    self ! PoisonPill
  }

}

object InstanceActorApp {
  def main(args:Array[String]) {
    val ins = ActorSystem("testsystem").actorOf(Props[InstanceActor], "instance")
    val nodeConfig = new NodeConfiguration(2000.0, 50000.0, 1000, 20)
    ins ! InstanceStart(nodeConfig)
  }
}
