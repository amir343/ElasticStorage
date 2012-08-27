package cloud

import common.NodeConfiguration
import util.InstanceGroupActor.{ name ⇒ instanceGroupName }
import cloud.gui.CloudGUI
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
import instance.os.CloudSnapshot
import instance.os.CloudSnapshot

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
  private lazy val costTable: mutable.ConcurrentMap[ActorRef, Double] = new ConcurrentHashMap[ActorRef, Double]()
  private lazy val periodicCostTable: mutable.ConcurrentMap[ActorRef, Double] = new ConcurrentHashMap[ActorRef, Double]()

  def receive = genericHandler orElse
    instanceHandler orElse
    healthCheckerHandler orElse
    uncategorizedMessagesHandler

  def genericHandler: Receive = {
    case CloudStart(cloudConfig, name)             ⇒ initialize(cloudConfig, name)
    case BlocksAck()                               ⇒ //TODO:
    case MyCPULoadAndBandwidth(cpuLoad, bandwidth) ⇒ showCPULoad(cpuLoad, bandwidth)
    case InstanceCost(totalCost, periodicCost)     ⇒ saveInstanceCost(totalCost, periodicCost)
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

  // TODO
  def initialize(nodeConfig: NodeConfiguration) {
    instanceGroup ! LaunchInstance(NodeConfiguration())
    //		if (!alreadyDefined) {
    //			Node node = getNewNodeInfo();
    //            gui.addNewInstance(node);
    //            numberOfInstances++;
    //			nodeConfiguration.setNodeInfo(node);
    //		}
    //		if (currentNodes.size() != 0) {
    //			trigger(new RebalanceDataBlocks(nodeConfiguration), elb);
    //		} else {
    //			trigger(new GetReplicas(nodeConfiguration), elb);
    //			logger.debug("Requesting replicas from ELB...");
    //			dnsService.addDNSEntry(nodeConfiguration.getNode());
    //		}
  }

  def kill(instanceName: String) {
    instanceGroup ! KillInstance(instanceName)
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
    log.info("Node %s initialized   [ok]" format instanceName)
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

  private def saveInstanceCost(totalCost: String, periodicCost: String) {
    gui.updateCostForNode(sender.path.toString, totalCost)
    costTable.put(sender, totalCost.toDouble)
    periodicCostTable.put(sender, periodicCost.toDouble)
  }

  private def showCPULoad(cpuLoad: Double, bandwidth: Long) {
    gui.updateCPULoadForNode(sender.path.toString, cpuLoad)
  }

  def stopActor() {
    log.info("CloudProvider %s is shutting down..." format _name)
    context.system.shutdown()
  }

  def takeSnapshot() {
    val cloudSnapshot = new CloudSnapshot(lastCreatedSnapshotId)
    lastCreatedSnapshotId += 1
    cloudSnapshot.chart = ResponseTimeService.chart
    gui.addSnapshot(cloudSnapshot);
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
