package instance

import _root_.common.GUI
import akka.actor._
import common._
import gui.{ HeadLessGUI, GenericInstanceGUI, InstanceGUI }
import cloud.common.NodeConfiguration
import os.{ CostService, Kernel }
import protocol._
import akka.util.duration._
import protocol.CPUInit
import protocol.CPULoadDiagram
import protocol.CPULoad
import protocol.UpdateCPUInfoLabel
import protocol.InstanceStart
import protocol.CPUReady
import protocol.CPULog
import org.jfree.data.xy.{ XYSeries, XYSeriesCollection }
import collection.mutable
import java.util.concurrent.{ ConcurrentLinkedQueue, ConcurrentHashMap }
import java.util.UUID
import scala.collection.JavaConversions._
import protocol.DiskReady
import protocol.CPUInit
import protocol.CPULoadDiagram
import protocol.MemoryInit
import protocol.MemoryInfoLabel
import protocol.MemoryLog
import protocol.LoadBlock
import protocol.BlockResponse
import protocol.RebalanceRequest
import protocol.CPULoad
import protocol.DiskInit
import protocol.UpdateCPUInfoLabel
import protocol.KernelLog
import scala.Some
import protocol.WaitTimeout
import protocol.MemoryReady
import protocol.ProcessRequestQueue
import protocol.InstanceStart
import protocol.AbstractOperation
import protocol.KernelInit
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

  // Components
  private val cpu = context.actorOf(Props[CPUActor])
  private val disk = context.actorOf(Props[DiskActor])
  private val memory = context.actorOf(Props[MemoryActor])
  private val kernel = context.actorOf(Props[KernelActor])

  // TODO: should be filled with correct actor by referencing the path ../cloudProvider
  private var cloudProvider: ActorRef = _

  private var uname_r = "2.2-2"
  private val numberOfDevices = 3
  private var numberOfDevicesLoaded = 0
  private var BANDWIDTH: Long = 2 * Size.MB.getSize
  private val WAIT: Long = 1000
  private val REQUEST_QUEUE_PROCESSING_INTERVAL: Long = 1000
  private val CPU_LOAD_PROPAGATION_INTERVAL: Long = 5000
  val RESTART_PERIOD: Long = 60000
  private val COST_CALCULATION_INTERVAL: Long = 10000
  private var simultaneousDownloads: Int = 70
  private var pt: mutable.ConcurrentMap[String, Process] = new ConcurrentHashMap[String, Process]
  private var currentTransfers: mutable.ConcurrentMap[UUID, String] = new ConcurrentHashMap[UUID, String]
  private val requestQueue = new ConcurrentLinkedQueue[Request]
  private var costService: CostService = new CostService
  protected var gui: GenericInstanceGUI with GUI = null
  protected var _nodeConfiguration: NodeConfiguration = null
  protected var currentCpuLoad: Double = .0
  private val dataSet: XYSeriesCollection = new XYSeriesCollection
  private val xySeries: XYSeries = new XYSeries("Load")
  private val startTime: Long = System.currentTimeMillis
  private var lastSnapshotID: Int = 1
  private var currentBandwidth: Long = BANDWIDTH
  private var blocks = List.empty[Block]
  private var enabled = true
  private var megaBytesDownloadedSoFar = 0
  private var totalCost = 0.0
  private var headless = false
  private var dataBlocks = mutable.Map.empty[String, ActorRef]

  def receive = genericHandler orElse
    kernelHandler orElse
    cpuHandler orElse
    diskHandler orElse
    memoryHandler orElse
    uncategorizedHandler

  def genericHandler: Receive = {
    case InstanceStart(nodeConfig) ⇒ initialize(nodeConfig)
    case WaitTimeout()             ⇒ handleWaitTimeout()
    case ProcessRequestQueue()     ⇒ processRequestQueue()
  }

  def kernelHandler: Receive = {
    case KernelLog(msg) ⇒ gui.log(msg)
  }

  def cpuHandler: Receive = {
    case CPUReady()                ⇒ numberOfDevicesLoaded += 1
    case CPULog(msg)               ⇒ gui.log(msg)
    case UpdateCPUInfoLabel(label) ⇒ gui.updateCPUInfoLabel(label)
    case CPULoadDiagram(chart)     ⇒ gui.createCPULoadDiagram(chart)
    case CPULoad(load)             ⇒ gui.cpuLoad(load)
  }

  def diskHandler: Receive = {
    case DiskReady()                   ⇒ numberOfDevicesLoaded += 1
    case BlockResponse(block, process) ⇒ //TODO
  }

  def memoryHandler: Receive = {
    case MemoryInfoLabel(label) ⇒ gui.updateMemoryInfoLabel(label)
    case MemoryReady()          ⇒ numberOfDevicesLoaded += 1
    case MemoryLog(msg)         ⇒ gui.log(msg)
  }

  def uncategorizedHandler: Receive = {
    case z ⇒ log.warning("Unrecognized or unhandled message: %s".format(z))
  }

  private def initialize(nodeConfig: NodeConfiguration) {
    retrieveInitParameters(nodeConfig)
    cpu ! CPUInit(nodeConfig)
    disk ! DiskInit(nodeConfig)
    memory ! MemoryInit(nodeConfig)
    loadKernel()
    loadBlocksToDisk()
    dataSet.addSeries(xySeries)
    waitForSystemStartUp()
    costService.init(nodeConfig)
    gui.updateBandwidthInfoLabel(Size.getSizeString(BANDWIDTH))
  }

  def stopActor() {
    log.info("Getting ready to die!")
    context.system.stop(self)
  }

  private def retrieveInitParameters(nodeConfig: NodeConfiguration) {
    _nodeConfiguration = nodeConfig
    BANDWIDTH = nodeConfig.getBandwidthConfiguration.getBandwidthMegaBytePerSecond
    simultaneousDownloads = nodeConfig.getSimultaneousDownloads()
    headless = nodeConfig.getHeadLess
    headless match {
      case true  ⇒ gui = new HeadLessGUI()
      case false ⇒ gui = InstanceGUI.getInstance()
    }
    log.info("NodeConfigurations:\n%s".format(nodeConfig.toString()))
    gui.updateSimultaneousDownloads(String.valueOf(simultaneousDownloads))
    gui.setInstanceReference(this)
  }

  private def loadBlocksToDisk() {
    if (_nodeConfiguration.getBlocks() != null) {
      blocks = _nodeConfiguration.getBlocks()
      //TODO
      //logger.info("Starting with " + blocks.size() + " block(s) in hand");
      disk ! LoadBlock(blocks)
      //TODO
      //cloudProvider ! BlocksAck()
      gui.initializeDataBlocks(blocks)
    } else {
      //TODO
      //logger.warn("I should get blocks from " + event.getNodeConfiguration().getDataBlocksMap().size() + " other instance(s)");
      dataBlocks = _nodeConfiguration.getBlocksMap()
      dataBlocks.keySet.foreach { b ⇒
        dataBlocks.get(b) match {
          case Some(actor) ⇒ actor ! RebalanceRequest(b)
          case None        ⇒ //TODO:Should not happen!
        }
      }
      addToBandwidthDiagram(BANDWIDTH)
    }
    //TODO
    //cloudProvider ! InstanceStarted()

  }

  private def loadKernel() {
    kernel ! KernelInit(_nodeConfiguration.getCpuConfiguration().getCpuSpeedInstructionPerSecond)
  }

  private def addToBandwidthDiagram(bandWidth: Long) {
    xySeries.add(System.currentTimeMillis - startTime, bandWidth)
  }

  private def waitForSystemStartUp() {
    context.system.scheduler.scheduleOnce(WAIT milliseconds, self, WaitTimeout())
  }

  private def handleWaitTimeout() {
    !instanceRunning match {
      case true ⇒ waitForSystemStartUp()
      case false ⇒
        //TODO
        context.system.scheduler.scheduleOnce(REQUEST_QUEUE_PROCESSING_INTERVAL milliseconds, self, ProcessRequestQueue())
      /*
        TODO
 				scheduleCPULoadPropagationToCloudProvider()
 				scheduleCostCalculation()
*/
    }
  }

  private def instanceRunning: Boolean = {
    numberOfDevices == numberOfDevicesLoaded && enabled
  }

  def processRequestQueue() {
    //TODO:Refactor this bloddy shit!
    if (instanceRunning) {
      val freeSlot = simultaneousDownloads - currentTransfers.size()
      cpu ! AbstractOperation(new MemoryCheckOperation)
      var i: Int = 0
      for (i ← 0 to freeSlot) {
        if (!requestQueue.isEmpty) {
          val req = requestQueue.remove()
          cpu ! AbstractOperation(new MemoryCheckOperation())
          cpu ! AbstractOperation(new MemoryReadOperation(8))
          startProcessForRequest(req)
        }
      }
      gui.updateRequestQueue(requestQueue.size())
      requestQueue.synchronized {
        for (j ← i to requestQueue.size()) {
          val req = requestQueue.remove()
          if (req != null)
            cloudProvider ! Rejected(req)
        }
      }
    }
    context.system.scheduler.scheduleOnce(REQUEST_QUEUE_PROCESSING_INTERVAL milliseconds, self, ProcessRequestQueue())
  }

  private def startProcessForRequest(req: Request) {
    //TODO
  }

}

object InstanceActorApp {
  def main(args: Array[String]) {
    val ins = ActorSystem("testsystem").actorOf(Props[InstanceActor], "instance")
    val nodeConfig = new NodeConfiguration(2.0, 50000.0, 1000, 20)
    nodeConfig.setHeadLess(false)
    ins ! InstanceStart(nodeConfig)
  }
}
