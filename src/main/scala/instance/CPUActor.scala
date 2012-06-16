package instance

import akka.actor.{ ActorLogging, ActorRef, Actor }
import akka.util.duration._
import gui.GenericInstanceGUI
import org.jfree.data.xy.{ XYSeries, XYSeriesCollection }
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ ConcurrentHashMap, ConcurrentMap }
import protocol._
import cloud.common.NodeConfiguration
import protocol.CPUInit
import protocol.LoadSamplerTimeout
import protocol.CPUReady
import protocol.CPULog
import collection.mutable.ListBuffer
import instance.os.Process
import instance.common.{ AbstractOperation ⇒ AOperation }
import org.jfree.chart.{ ChartFactory, JFreeChart }
import org.jfree.chart.plot.PlotOrientation

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
class CPUActor extends Actor with ActorLogging {

  private var CPU_CLOCK: Long = 2000000000L
  private val LOAD_CALC_INTERVAL: Long = 5000
  private val SAMPLER_INTERVAL: Long = 500
  private val startTime: Long = System.currentTimeMillis
  private val dataSet: XYSeriesCollection = new XYSeriesCollection
  private val xySeries: XYSeries = new XYSeries("Load")
  private val tasks: AtomicInteger = new AtomicInteger
  private val pt = new ConcurrentHashMap[String, Process]()
  private val loadSamples = ListBuffer.empty[Int]
  private val loads = ListBuffer.empty[Double]
  protected var enabled: Boolean = false
  protected var gui: GenericInstanceGUI = null
  private var _nodeConfig: NodeConfiguration = _
  private var instance: ActorRef = _

  tasks.set(0)

  def receive = {
    case CPUInit(nodeConfig)           ⇒ { instance = sender; initialize(nodeConfig) }
    case LoadSamplerTimeout()          ⇒ collectLoad()
    case LoadCalculationTimeout()      ⇒ calculateLoad()
    case RestartSignal()               ⇒ handleRestartSignal()
    case Restart()                     ⇒ restart()
    case StartProcess(p)               ⇒ startProcess(p)
    case EndProcess(p)                 ⇒ endProcess(p)
    case AbstractOperation(operation)  ⇒ handleAbstractOperation(operation)
    case OperationFinishedTimeout(pid) ⇒ operationFinished(pid)
    case SnapshotRequest(_)            ⇒ handleSnapshot()
  }

  private def initialize(nodeConfig: NodeConfiguration) {
    enabled = true
    _nodeConfig = nodeConfig
    CPU_CLOCK = nodeConfig.getCpuConfiguration.getCpuSpeedInstructionPerSecond
    instance ! UpdateCPUInfoLabel("%s GHz".format(nodeConfig.getCpuConfiguration.getCpuSpeed))
    dataSet.addSeries(xySeries)
    printCPULog()
    sendReadySignal()
    context.system.scheduler.schedule(1000 milliseconds, SAMPLER_INTERVAL milliseconds, self, LoadSamplerTimeout())
    context.system.scheduler.schedule(LOAD_CALC_INTERVAL milliseconds, LOAD_CALC_INTERVAL milliseconds, self, LoadCalculationTimeout())
    log.debug("CPU is initialized")
    instance ! CPULoadDiagram(getChart)
  }

  private def printCPULog() {
    val sb = new StringBuilder()
    sb.append(" CPU: Unsupported number of siblings 4\n")
    sb.append("CPU Intel ").append(_nodeConfig.getCpuConfiguration().getCpuSpeed).append(" (GHz) core i7 started...")
    instance ! CPULog(sb.toString())
  }

  private def sendReadySignal() { instance ! CPUReady() }

  private def handleRestartSignal() {
    if (enabled) {
      log.debug("Received Restart signal")
      enabled = false
      xySeries.clear()
      pt.clear()
      loadSamples.clear()
      loads.clear()
      tasks.set(0)
      instance ! CPULoadDiagram(getChart)
      context.system.scheduler.scheduleOnce(1000 milliseconds, self, Restart())
    }
  }

  private def restart() {
    enabled = true
    instance ! CPUReady()
    instance ! CPULoadDiagram(getChart)
  }

  private def calculateLoad() {
    if (enabled) {
      val load = loadSamples.size match {
        case 0 ⇒ 1
        case n ⇒ loadSamples.sum / n
      }
      loadSamples.clear()
      loads += load
      instance ! CPULoad(load)
      instance ! CPULoad(load)
      xySeries.add((System.currentTimeMillis() - startTime), load)
      instance ! CPULoadDiagram(getChart)
    }
  }

  private def endProcess(process: Process) {
    if (enabled) {
      pt.remove(process.getPid)
      tasks decrementAndGet ()
    }
  }

  private def handleAbstractOperation(operation: AOperation) {
    if (enabled) {
      tasks incrementAndGet ()
      for (i ← 1 until operation.getNumberOfOperations()) {
        val p = Process.createAbstractProcess()
        pt.put(p.getPid, p)
        context.system.scheduler.scheduleOnce(operation.getDuration(CPU_CLOCK) milliseconds, self, OperationFinishedTimeout(p.getPid))
      }
    }
  }

  private def handleSnapshot() {
    if (enabled) {
      log.debug("Received Snapshot request")
      instance ! SnapshotRequest(getChart)
    }
  }

  private def operationFinished(pid: String) {
    if (enabled) {
      pt.remove(pid)
      tasks decrementAndGet ()
    }
  }

  private def startProcess(process: Process) {
    if (enabled) {
      pt.put(process.getPid, process)
      tasks incrementAndGet ()
    }
  }

  private def collectLoad() {
    if (enabled) {
      loads += pt.size()
    }
  }

  private def getChart: JFreeChart = ChartFactory.createXYLineChart("CPU Load", "Time (ms)", "Cpu Load", dataSet, PlotOrientation.VERTICAL, true, true, false)

}
