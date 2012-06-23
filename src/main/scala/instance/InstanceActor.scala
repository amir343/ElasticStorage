package instance

import _root_.common.GUI
import akka.actor._
import common._
import cpu.{ OperationDuration, CPU }
import gui.{ HeadLessGUI, GenericInstanceGUI, InstanceGUI }
import cloud.common.NodeConfiguration
import os._
import protocol._
import akka.util.duration._
import org.jfree.data.xy.{ XYSeries, XYSeriesCollection }
import collection.mutable
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConversions._
import scheduler.SchedulerActor
import cloud.CloudProviderActor
import org.jfree.chart.{ ChartFactory, JFreeChart }
import org.jfree.chart.plot.{ XYPlot, PlotOrientation }
import org.jfree.chart.renderer.xy.XYItemRenderer
import java.awt.Color
import java.text.DecimalFormat
import logger.{ LoggerFactory, Logger }
import protocol.DiskReady
import protocol.CPUInit
import protocol.Rejected
import protocol.HeartBeatMessage
import protocol.ReadBlock
import protocol.CPULoadDiagram
import protocol.MemoryInit
import protocol.Shutdown
import protocol.MemoryInfoLabel
import protocol.MemoryLog
import protocol.LoadBlock
import protocol.MyCPULoadAndBandwidth
import protocol.RequestMessage
import protocol.CalculateCost
import protocol.CancellableSchedule
import protocol.BlockResponse
import protocol.DownloadStarted
import protocol.RebalanceRequest
import protocol.CPULoad
import protocol.DiskInit
import protocol.ShutdownAck
import protocol.RequestBlock
import protocol.UpdateCPUInfoLabel
import protocol.Schedule
import protocol.Death
import protocol.EndProcess
import protocol.InstanceStarted
import protocol.KernelLog
import scala.Some
import protocol.WaitTimeout
import protocol.SnapshotRequest
import protocol.MemoryReady
import protocol.AckBlock
import protocol.StartProcess
import protocol.Alive
import protocol.CloseMyStream
import protocol.ProcessRequestQueue
import protocol.NackBlock
import protocol.WriteBlockIntoMemory
import protocol.BlockTransferred
import protocol.KernelLoaded
import protocol.InstanceStart
import protocol.AbstractOperation
import protocol.ReadDiskFinished
import protocol.CancelProcess
import protocol.PropagateCPULoad
import protocol.KernelInit
import protocol.BlocksAck
import protocol.CPUReady
import protocol.CPULog
import protocol.TransferringFinished
import java.util

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

  private val scheduler = context.actorFor("../scheduler")
  private val cloudProvider = context.actorFor("../cloudProvider")

  private val uname_r = "2.2-2"
  private val numberOfDevices = 3
  private var numberOfDevicesLoaded = 0
  private var BANDWIDTH: Long = 2 * Size.MB.getSize
  private val WAIT: Long = 1000
  private val REQUEST_QUEUE_PROCESSING_INTERVAL: Long = 1000
  private val CPU_LOAD_PROPAGATION_INTERVAL: Long = 5000
  val RESTART_PERIOD: Long = 60000
  private val COST_CALCULATION_INTERVAL: Long = 10000
  private var simultaneousDownloads: Int = 70
  private val pt: mutable.ConcurrentMap[String, Process] = new ConcurrentHashMap[String, Process]
  private val currentTransfers: mutable.ConcurrentMap[String, Cancellable] = new ConcurrentHashMap[String, Cancellable]
  private val requestQueue = mutable.Queue.empty[Request]
  private val costService: CostService = new CostService
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
  private var kernelLoaded = false
  //TODO: should not use the old logger, instead call directly gui.log
  private var guiLogger: Logger = _

  def receive = genericHandler orElse
    cloudHandler orElse
    kernelHandler orElse
    cpuHandler orElse
    diskHandler orElse
    memoryHandler orElse
    cancellableHandler orElse
    uncategorizedHandler

  private def genericHandler: Receive = {
    case InstanceStart(nodeConfig)       ⇒ initialize(nodeConfig)
    case WaitTimeout()                   ⇒ handleWaitTimeout()
    case ProcessRequestQueue()           ⇒ processRequestQueue()
    case PropagateCPULoad()              ⇒ propagateCPULoad()
    case CalculateCost()                 ⇒ calculateCost()
    case ReadDiskFinished(pid)           ⇒ handleReadDiskFinished(pid)
    case TransferringFinished(pid)       ⇒ handleTransferringFinished(pid)
    case Shutdown()                      ⇒ handleShutdown()
    case Death()                         ⇒ stopActor()
    case CloseMyStream()                 ⇒ handleCloseMyStreamRequest()
    case BlockTransferred(blockId, size) ⇒ handleBlockTransferred(blockId, size)
    case RebalanceRequest(blockId)       ⇒ handleRebalanceRequest(blockId)
    case RebalanceResponse(block)        ⇒ handleRebalanceResponse(block)
    case PostRebalancingActivities()     ⇒ handlePostRebalancingActivities()
  }

  private def cloudHandler: Receive = {
    case RequestMessage(request) ⇒ handleRequest(request)
    case RestartInstance()       ⇒ restartInstance()
    case HeartBeatMessage()      ⇒ sender ! Alive()
  }

  private def kernelHandler: Receive = {
    case KernelLog(msg) ⇒ gui.log(msg)
    case KernelLoaded() ⇒ osStarted()
  }

  private def cpuHandler: Receive = {
    case CPUReady()                ⇒ handleCPUReady()
    case CPULog(msg)               ⇒ gui.log(msg)
    case UpdateCPUInfoLabel(label) ⇒ gui.updateCPUInfoLabel(label)
    case CPULoadDiagram(chart)     ⇒ gui.createCPULoadDiagram(chart)
    case CPULoad(load)             ⇒ handleCPULoad(load)
    case SnapshotRequest(chart)    ⇒ handleSnapshotRequest(chart)
  }

  private def diskHandler: Receive = {
    case DiskReady()                   ⇒ handleDiskReady()
    case BlockResponse(block, process) ⇒ handleBlockResponse(block, process)
  }

  private def memoryHandler: Receive = {
    case AckBlock(process)      ⇒ handleAckBlock(process)
    case NackBlock(process)     ⇒ handleNackBlock(process)
    case MemoryInfoLabel(label) ⇒ gui.updateMemoryInfoLabel(label)
    case MemoryReady()          ⇒ handleMemoryReady()
    case MemoryLog(msg)         ⇒ gui.log(msg)
  }

  private def cancellableHandler: Receive = {
    case CancelProcess(pid, cancellable) ⇒ currentTransfers.put(pid, cancellable)
  }

  private def uncategorizedHandler: Receive = {
    case z @ _ ⇒ log.warning("Unrecognized or unhandled message: %s".format(z))
  }

  private def handleRequest(request: Request) {
    if (instanceRunning) {
      guiLogger.debug("Received request for block %s".format(request))
      (simultaneousDownloads > currentTransfers.size()) match {
        case true ⇒
          guiLogger.debug("Admitted Request for block '%s'".format(request.getBlockId))
          requestQueue.enqueue(request)
        case false ⇒
          guiLogger.warn("Rejected Request for download block %s. No free slot. {simDown: %s, currentTrans: %s}".format(request.getBlockId, simultaneousDownloads, currentTransfers.size))
          cloudProvider ! Rejected(request)
      }
    }
  }

  private def handleBlockResponse(block: Block, process: Process) {
    if (instanceRunning) {
      readFromDiskIntoMemory(block, process)
      cpu ! AbstractOperation(new DiskReadOperation(block.getSize))
      cpu ! AbstractOperation(new MemoryWriteOperation(block.getSize))
      memory ! WriteBlockIntoMemory(block)
    }
  }

  private def readFromDiskIntoMemory(block: Block, process: Process) {
    process.setBlockSize(block.getSize)
    scheduler ! Schedule(OperationDuration.getDiskReadDuration(CPU.CPU_CLOCK, block.getSize), self, ReadDiskFinished(process.getPid))
  }

  private def handleReadDiskFinished(pid: String) {
    if (instanceRunning) {
      pt.get(pid) match {
        case Some(process) ⇒ scheduleTransferForBlock(process)
        case None          ⇒ // Process already finished!
      }
      cpu ! AbstractOperation(new MemoryCheckOperation())
    }
  }

  private def handleRebalanceRequest(blockId: String) {
    blocks.find(_.getName == blockId) match {
      case Some(block) ⇒
        sender ! RebalanceResponse(block)
        val req = new Request(util.UUID.randomUUID().toString, block.getName, sender)
        startProcessForRequest(req)
      case None ⇒ log.error("Received rebalance request for %s that I don't have!".format(blockId))
    }
  }

  private def handleRebalanceResponse(block: Block) {
    blocks = blocks ::: List(block)
    guiLogger.debug("Rebalancing is started from %s for block %s".format(sender, block.getName))
    scheduler ! Schedule(1000, self, PostRebalancingActivities())
  }

  private def handlePostRebalancingActivities() {
    instanceRunning match {
      case true ⇒
        val p = Process.createAbstractProcess()
        pt.put(p.getPid, p)
        cpu ! StartProcess(p)
        currentBandwidth = BANDWIDTH / blocks.size()
        addToBandwidthDiagram(currentBandwidth)
      case false ⇒
        scheduler ! Schedule(1000, self, PostRebalancingActivities())
    }
  }

  private def handleCPUReady() {
    numberOfDevicesLoaded += 1
    for (i ← 1 to 10)
      cpu ! AbstractOperation(new BootOperation())
    guiLogger.debug("Received ready signal from CPU")
    if (instanceRunning) osStarted()
  }

  private def handleMemoryReady() {
    numberOfDevicesLoaded += 1
    guiLogger.debug("Received ready signal from MEMORY")
    if (instanceRunning) osStarted()
  }

  private def handleDiskReady() {
    numberOfDevicesLoaded += 1
    guiLogger.debug("Received ready signal from DISK")
    if (instanceRunning) osStarted()
  }

  private def osStarted() {
    enabled = true
    kernelLoaded = true
    guiLogger.debug("OS with kernel %s started".format(uname_r))
    gui.decorateSystemStarted()
  }

  private def handleNackBlock(process: Process) {
    if (instanceRunning) {
      guiLogger.debug("Block %s does not exists in the memory".format(process.getRequest.getBlockId))
      disk ! ReadBlock(process.getRequest.getBlockId, process)
    }
  }

  def restartInstance() {
    if (instanceRunning) {
      kernelLoaded = false
      cpu ! RestartSignal()
      memory ! RestartSignal()
      disk ! RestartSignal()
      gui.systemRestart()

      guiLogger.warn("System restarting...")
      numberOfDevicesLoaded = 0
      pt.clear()
      cancelAllPreviousTimers()
      currentTransfers.clear()
      requestQueue.clear()
      xySeries.clear()
      scheduleCPULoadPropagationToCloudProvider()
      kernel ! Shutdown()
      gui.decorateWhileSystemStartUp()

      memory ! MemoryInit(_nodeConfiguration)
      disk ! DiskInit(_nodeConfiguration)
      loadKernel()
      scheduleCPULoadPropagationToCloudProvider()
      scheduler ! Schedule(REQUEST_QUEUE_PROCESSING_INTERVAL, self, ProcessRequestQueue())

    }
  }

  private def handleSnapshotRequest(chart: JFreeChart) {
    if (instanceRunning) {
      val snapshot = new InstanceSnapshot(lastSnapshotID)
      lastSnapshotID += 1
      snapshot.addCPULoadChart(chart)
      snapshot.addBandwidthChart(getBandwidthChart)
      gui.addSnapshot(snapshot)
    }
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
      case true ⇒
        gui = new HeadLessGUI()
        guiLogger = LoggerFactory.getLogger(classOf[InstanceActor], gui)
      case false ⇒
        gui = InstanceGUI.getInstance()
        guiLogger = LoggerFactory.getLogger(classOf[InstanceActor], gui)
    }
    log.info("NodeConfigurations:\n%s".format(nodeConfig.toString()))
    gui.updateSimultaneousDownloads(String.valueOf(simultaneousDownloads))
    gui.setInstanceReference(this)
  }

  private def loadBlocksToDisk() {
    if (_nodeConfiguration.getBlocks() != null) {
      blocks = _nodeConfiguration.getBlocks()
      guiLogger.info("Starting with %s block(s) in hand".format(blocks.size))
      disk ! LoadBlock(blocks)
      cloudProvider ! BlocksAck()
      gui.initializeDataBlocks(blocks)
    } else {
      guiLogger.warn("I should get blocks from %s other instance(s)".format(_nodeConfiguration.getDataBlocksMap().size()))
      dataBlocks = _nodeConfiguration.getBlocksMap()
      dataBlocks.keySet.foreach { b ⇒
        dataBlocks.get(b) match {
          case Some(actor) ⇒ actor ! RebalanceRequest(b)
          case None        ⇒ //Should not happen!!
        }
      }
      addToBandwidthDiagram(BANDWIDTH)
    }
    cloudProvider ! InstanceStarted()
  }

  private def loadKernel() {
    gui.updateTitle(context.self.path.address.toString)
    gui.decorateWhileSystemStartUp()
    kernel ! KernelInit(_nodeConfiguration.getCpuConfiguration().getCpuSpeedInstructionPerSecond)
  }

  private def addToBandwidthDiagram(bandWidth: Long) {
    xySeries.add(System.currentTimeMillis - startTime, bandWidth)
  }

  private def waitForSystemStartUp() {
    scheduler ! Schedule(WAIT, self, WaitTimeout())
  }

  private def handleWaitTimeout() {
    !instanceRunning match {
      case true ⇒ waitForSystemStartUp()
      case false ⇒
        scheduler ! Schedule(REQUEST_QUEUE_PROCESSING_INTERVAL, self, ProcessRequestQueue())
        scheduleCPULoadPropagationToCloudProvider()
        scheduleCostCalculation()
    }
  }

  private def scheduleCPULoadPropagationToCloudProvider() {
    scheduler ! Schedule(CPU_LOAD_PROPAGATION_INTERVAL, self, PropagateCPULoad())
    gui.createBandwidthDiagram(getBandwidthChart)
  }

  private def scheduleCostCalculation() {
    scheduler ! Schedule(COST_CALCULATION_INTERVAL, self, CalculateCost())
  }

  private def calculateCost() {
    totalCost = costService.computeCostSoFar(megaBytesDownloadedSoFar)
    val costToSend: Double = costService.computeCostInThisPeriod(megaBytesDownloadedSoFar)
    val df: DecimalFormat = new DecimalFormat("##.####")
    val totalCostString: String = df.format(totalCost)
    val periodicCostString: String = df.format(costToSend)
    gui.updateCurrentCost(totalCostString)
    cloudProvider ! InstanceCost(totalCostString, periodicCostString)
    scheduleCostCalculation()
  }

  private def instanceRunning = numberOfDevices == numberOfDevicesLoaded && enabled && kernelLoaded

  private def processRequestQueue() {
    if (instanceRunning) {
      val freeSlot = simultaneousDownloads - currentTransfers.size()
      cpu ! AbstractOperation(new MemoryCheckOperation)
      val taken = requestQueue.take(freeSlot)
      val diff = requestQueue diff taken
      requestQueue.clear()
      requestQueue ++= diff
      taken.foreach { req ⇒
        cpu ! AbstractOperation(new MemoryCheckOperation())
        cpu ! AbstractOperation(new MemoryReadOperation(8))
        startProcessForRequest(req)
      }
      gui.updateRequestQueue(requestQueue.size())
      requestQueue.synchronized {
        requestQueue.dequeueAll(r ⇒ true).filter(_ != null).foreach(cloudProvider ! Rejected(_))
      }
    }
    scheduler ! Schedule(REQUEST_QUEUE_PROCESSING_INTERVAL, self, ProcessRequestQueue())
  }

  private def startProcessForRequest(req: Request) {
    val process = new Process(req)
    cpu ! StartProcess(process)
    pt.put(process.getPid, process)
    memory ! RequestBlock(process)
    gui.increaseNrDownloadersFor(req.getBlockId)
    gui.updateCurrentTransfers(currentTransfers.size())
  }

  private def handleAckBlock(process: Process) {
    if (instanceRunning) {
      guiLogger.debug("Block %s exists in the memory".format(process.getRequest.getBlockId))
      scheduleTransferForBlock(process)
      cpu ! AbstractOperation(new MemoryReadOperation(process.getBlockSize))
    }
  }

  private def scheduleTransferForBlock(process: Process) {
    currentTransfers.size() match {
      case 0 ⇒
        guiLogger.debug("Transfer started %s".format(process))
        scheduleTimeoutFor(process, BANDWIDTH)
        addToBandwidthDiagram(BANDWIDTH)
        currentBandwidth = BANDWIDTH
      case _ ⇒
        cpu ! AbstractOperation(new MemoryCheckOperation())
        val newBandwidth = BANDWIDTH / (currentTransfers.size() + 1)
        val now = System.currentTimeMillis()
        cancelAllPreviousTimers()
        rescheduleAllTimers(newBandwidth, now)
        guiLogger.debug("Transfer started %s".format(process))
        scheduleTimeoutFor(process, newBandwidth)
    }
    cloudProvider ! DownloadStarted(process.getRequest.getId)
  }

  private def cancelAllPreviousTimers() {
    val ctClone = currentTransfers.clone()
    currentTransfers.clear()
    ctClone.values.foreach { c ⇒
      c.cancel()
      cpu ! AbstractOperation(new MemoryCheckOperation())
    }
  }

  private def rescheduleAllTimers(newBandwidth: Long, now: Long) {
    guiLogger.debug("Rescheduling all current downloads with bandwidth: %s B/s".format(newBandwidth))
    addToBandwidthDiagram(newBandwidth)
    currentBandwidth = newBandwidth

    val currentTransferClone = currentTransfers.clone()
    currentTransfers.clear()

    currentTransferClone.foreach { c ⇒
      cpu ! AbstractOperation(new MemoryCheckOperation())
      pt.get(c._1) match {
        case Some(p) ⇒
          p.setRemainingBlockSize(p.getRemainingBlockSize - (now - p.getSnapshot) * p.getCurrentBandwidth / 1000)
          if (p.getRemainingBlockSize < 0) p.setRemainingBlockSize(0)
          p.setCurrentBandwidth(newBandwidth)
          p.setTimeout(p.getRemainingBlockSize / p.getCurrentBandwidth)
          p.setSnapshot(now)
          pt.put(p.getPid, p)
          val duration: Long = 1000 * p.getRemainingBlockSize / p.getCurrentBandwidth
          scheduler ! CancellableSchedule(duration, self, TransferringFinished(p.getPid), CancelProcess(p.getPid))
        case None ⇒ //TODO:What should we do?
      }
    }
  }

  private def scheduleTimeoutFor(process: Process, bandwidth: Long) {
    cpu ! AbstractOperation(new MemoryCheckOperation())
    val transferDelay = 1000 * process.getBlockSize / bandwidth
    val pid = process.getPid
    val duration: Long = 1000L * process.getBlockSize / bandwidth
    scheduler ! CancellableSchedule(duration, self, TransferringFinished(pid), CancelProcess(pid))
    process.setCurrentBandwidth(bandwidth).setRemainingBlockSize(process.getBlockSize).setSnapshot(System.currentTimeMillis()).setTimeout(transferDelay)
    pt.put(pid, process)
  }

  private def handleTransferringFinished(pid: String) {
    if (instanceRunning) {
      pt.get(pid) match {
        case Some(process) ⇒
          val request = process.getRequest
          updateTransferredBandwidth(process)
          currentTransfers.get(pid) match {
            case Some(c) ⇒
              c.cancel()
              currentTransfers -= pid
            case None ⇒ //NOP
          }
          gui.decreaseNrDownloadersFor(request.getBlockId)
          gui.updateCurrentTransfers(currentTransfers.size())
          informDownloader(process, request)
          pt -= pid
          cpu ! EndProcess(pid)
          if (currentTransfers.size() != 0) {
            cancelAllPreviousTimers()
            rescheduleAllTimers(BANDWIDTH / currentTransfers.size(), System.currentTimeMillis())
          }
        case None ⇒ //NOP
      }
    }
  }

  private def informDownloader(process: Process, request: Request) {
    request.getDestNode match {
      case null ⇒
        guiLogger.debug("Transferring finished for %s".format(process.getPid))
      case z if z != null ⇒
        guiLogger.info("Rebalancing finished for %s".format(process.getPid))
        z ! BlockTransferred(request.getId, process.getBlockSize)
    }
  }

  private def updateTransferredBandwidth(process: Process) {
    // TODO: Should it be synchronized?
    megaBytesDownloadedSoFar += (process.getBlockSize / (1024 * 1024)).asInstanceOf[Int]
  }

  private def getBandwidthChart: JFreeChart = {
    val chart: JFreeChart = ChartFactory.createXYLineChart("Bandwidth per download", "Time (ms)", "Bandwidth (B/s)", dataSet, PlotOrientation.VERTICAL, true, true, false)
    val plot: XYPlot = chart.getXYPlot
    val renderer: XYItemRenderer = plot.getRenderer
    renderer.setSeriesPaint(0, Color.blue)
    chart
  }

  private def propagateCPULoad() {
    if (instanceRunning) {
      cloudProvider ! MyCPULoadAndBandwidth(currentCpuLoad, currentBandwidth)
      cpu ! AbstractOperation(new MemoryCheckOperation())
      scheduleCPULoadPropagationToCloudProvider()
    }
  }

  private def handleShutdown() {
    sender match {
      case `cloudProvider` ⇒
        guiLogger.debug("Time to die forever :(")
        if (!headless) gui.asInstanceOf[InstanceGUI].dispose()
        dataBlocks.foreach(_._2 ! CloseMyStream())
        cloudProvider ! ShutdownAck()
        scheduler ! Schedule(1000, self, Death())
      case _ ⇒ guiLogger.warn("Oh oh! Someone is trying to kill me, help!")
    }
  }

  private def handleBlockTransferred(blockId: String, size: Long) {
    pt.find((en) ⇒ en._2.getRequest.getBlockId == blockId) match {
      case Some((pid, process)) ⇒
        pt -= pid
        val block = new Block(blockId, size)
        cloudProvider ! ActivateBlock(block)
        cpu ! EndProcess(pid)
        cpu ! AbstractOperation(new DiskWriteOperation(size))
        currentBandwidth = pt.size match {
          case 0 ⇒ BANDWIDTH
          case _ ⇒ BANDWIDTH / pt.size
        }
        addToBandwidthDiagram(currentBandwidth)
        if (blocks.size == dataBlocks.size) {
          guiLogger.info("Starting with %s block(s) in hand".format(blocks.size()))
          disk ! LoadBlock(blocks)
          gui.initializeDataBlocks(blocks)
        }
      case None ⇒ //NOP
    }
  }

  private def handleCloseMyStreamRequest() {
    val removableEntries = pt.filter((kv) ⇒ kv._2.getRequest.getDestNode != null && kv._2.getRequest.getDestNode == sender).keys
    pt --= removableEntries
  }

  private def handleCPULoad(load: Double) {
    if (instanceRunning) {
      gui.cpuLoad(load)
      currentCpuLoad = load
    }
  }

  def takeSnapshot() {
    cpu ! SnapshotRequest()
  }

}

object InstanceActorApp {
  def main(args: Array[String]) {
    val system = ActorSystem("testsystem")
    val scheduler = system.actorOf(Props[SchedulerActor], "scheduler")
    val cloudProvider = system.actorOf(Props[CloudProviderActor], "cloudProvider")
    val instance = system.actorOf(Props[InstanceActor], "instance")
    val nodeConfig = new NodeConfiguration(2.0, 5.0, 4, 20)
    nodeConfig.setHeadLess(false)
    instance ! InstanceStart(nodeConfig)
  }
}
