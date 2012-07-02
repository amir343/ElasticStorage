package cloud

import util.InstanceGroupActor.{ name ⇒ instanceGroupName }
import gui.CloudGUI
import scala.collection.JavaConversions._
import akka.actor._
import protocol._
import collection.mutable
import java.util.concurrent.ConcurrentHashMap
import protocol.MyCPULoadAndBandwidth
import protocol.InstanceStarted
import protocol.CloudStart
import protocol.InstanceCost
import protocol.BlocksAck
import protocol.CloudConfiguration

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

  log.info("CloudProvider is initialized: %s".format(self.path))

  // components
  private lazy val elb = context.system.actorOf(Props[ElasticLoadBalancer])
  private lazy val healthChecker = context.system.actorOf(Props[HealthCheckerActor])

  private lazy val controllerRef: ActorRef = context.system.actorFor("/user/controller")
  private lazy val instanceGroup: ActorRef = context.system.actorFor("/user/%s".format(instanceGroupName))

  private var _name: String = _
  private var lastCreatedSnapshotId = 1
  private var headLess: Boolean = false
  private var gui: CloudGUI = _
  //    private int lastCreatedElasticStorageNode = 1;
  private var _cloudConfiguration: CloudConfiguration = _
  private var connectedToController: Boolean = false
  //    private List<Node> currentNodes = new ArrayList<Node>();
  private lazy val costTable: mutable.ConcurrentMap[String, Double] = new ConcurrentHashMap[String, Double]()
  private lazy val periodicCostTable: mutable.ConcurrentMap[String, Double] = new ConcurrentHashMap[String, Double]()

  def receive = genericHandler orElse
    instanceHandler orElse
    healthCheckerHandler orElse
    uncategorizedMessagesHandler

  def genericHandler: Receive = {
    case CloudStart(cloudConfig, name)             ⇒ initialize(cloudConfig, name)
    case BlocksAck()                               ⇒ //TODO:
    case MyCPULoadAndBandwidth(cpuLoad, bandwidth) ⇒ //TODO
    case InstanceCost(totalCost, periodicCost)     ⇒ //TODO
  }

  def instanceHandler: Receive = {
    case InstanceStarted() ⇒ handleInstanceStarted()
  }

  def healthCheckerHandler: Receive = {
    case Suspect(instance) ⇒ suspectNode(instance)
    case Restore(instance) ⇒ restoreNode(instance)
  }

  def uncategorizedMessagesHandler: Receive = {
    case z @ _ ⇒ log.debug("Unrecognized message: %s".format(z))
  }

  override def postStop() {
    gui.disposeGUI()
  }

  private def initialize(cloudConfig: CloudConfiguration, name: String) {
    _cloudConfiguration = cloudConfig
    _name = name
    headLess = cloudConfig.headless
    setupGui()
    elb ! ELBInit(cloudConfig)
    healthChecker ! HealthCheckerInit()
  }

  private def handleInstanceStarted() {
    val instanceName = sender.path.toString
    gui.instanceStarted(instanceName)
    gui.instanceAdded()
    //			currentNodes.add(event.getNode());
    log.info("Node %s initialized   [ok]".format(instanceName))
    healthChecker ! ConsiderInstance(sender)
    if (connectedToController)
      controllerRef ! NewNodeToMonitor(sender)
  }

  private def suspectNode(node: ActorRef) {
    elb ! Suspect(node)
    gui.suspectInstance(node.path.toString)
  }

  private def restoreNode(node: ActorRef) {
    elb ! Restore(node)
    gui.restoreInstance(node.path.toString)
  }

  private def setupGui() {
    gui = new CloudGUI()
    gui.setTitle(self.path.toString)
    gui.setCloudProvider(this)
  }

  def stopActor() {
    log.info("CloudProvider %s is shutting down...".format(_name))
    context.system.shutdown()
  }

}

object CloudProviderApp {
  def main(args: Array[String]) {
    import util.CloudProviderActor
    val system = ActorSystem("test")
    val config = CloudConfiguration(List())
    val cloud = system.actorOf(Props[CloudProviderActor], CloudProviderActor.name)
    cloud ! CloudStart(config, "AmirProvider")
  }
}
