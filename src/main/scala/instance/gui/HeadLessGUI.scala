/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
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
 */
package instance.gui

import java.io.File
import instance.os.{OS, InstanceSnapshot}
import org.jfree.chart.JFreeChart
import java.util.List
import instance.common.Block

/**
 * @author Amir Moulavi
 * @date 2011-07-14
 *       This implementation will be used when the headless for instance is requested
 *
 */
class HeadLessGUI extends GenericInstanceGUI {

  def cpuLoad(load: Double) {}

  def initializeDataBlocks(blocks: List[Block]) {}

  def increaseNrDownloadersFor(blockId: String) {}

  def decreaseNrDownloadersFor(blockId: String) {}

  def resetNrDownloaders() {}

  def createCPULoadDiagram(chart: JFreeChart) {}

  def createBandwidthDiagram(chart: JFreeChart) {}

  def updateCPUInfoLabel(info: String) {}

  def updateMemoryInfoLabel(info: String) {}

  def updateBandwidthInfoLabel(info: String) {}

  def setOSReference(os: OS) {}

  def takeSnapshot() {}

  def addSnapshot(snapshot: InstanceSnapshot) {}

  def getSnapshotTable = null

  def deleteAllSnapshots() {}

  def saveAllSnapshotsTo(selectedDir: File) {}

  def systemRestart() {}

  def restartOS() {}

  def decorateWhileSystemStartUp() {}

  def decorateSystemStarted() {}

  def updateCurrentCost(cost: String) {}

  def updateTitle(title: String) {}

  def updateCurrentTransfers(n: Int) {}

  def updateRequestQueue(n: Int) {}

  def updateSimultaneousDownloads(info: String) {}
}