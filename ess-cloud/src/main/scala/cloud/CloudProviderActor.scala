package cloud

import scala.collection.JavaConversions._
import akka.actor.{ ActorRef, ActorLogging, Actor }
import gui.CloudGUI
import protocol._
import collection.mutable
import java.util.concurrent.ConcurrentHashMap
import protocol.MyCPULoadAndBandwidth
import protocol.InstanceStarted
import protocol.InstanceCost
import protocol.BlocksAck

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
class CloudProviderActor extends Actor with ActorLogging {

  log.info("CloudProvider is initialized.")

  private var controllerRef: ActorRef = context.system.actorFor("/user/controller")

  private var lastCreatedSnapshotId = 1
  private var headLess: Boolean = false
  private var gui: CloudGUI = _
  //    private int lastCreatedElasticStorageNode = 1;
  private var _cloudConfiguration: CloudConfiguration = _
  private var connectedToController: Boolean = false
  //    private List<Node> currentNodes = new ArrayList<Node>();
  private val costTable: mutable.ConcurrentMap[String, Double] = new ConcurrentHashMap[String, Double]()
  private val periodicCostTable: mutable.ConcurrentMap[String, Double] = new ConcurrentHashMap[String, Double]()

  def receive = genericHandler orElse
    uncategorizedMessagesHandler

  def genericHandler: Receive = {
    case BlocksAck()                               ⇒ //TODO:
    case InstanceStarted()                         ⇒ //TODO:
    case MyCPULoadAndBandwidth(cpuLoad, bandwidth) ⇒ //TODO
    case InstanceCost(totalCost, periodicCost)     ⇒ //TODO
  }

  def uncategorizedMessagesHandler: Receive = {
    case z @ _ ⇒ log.debug("Unrecognized message: %s".format(z))
  }

}
