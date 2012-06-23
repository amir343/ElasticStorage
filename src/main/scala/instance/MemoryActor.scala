package instance

import akka.actor.{ ActorRef, ActorLogging, Actor }
import common.{ Size, Block }
import protocol._
import cloud.common.NodeConfiguration
import protocol.MemoryInit
import protocol.MemoryInfoLabel
import protocol.MemoryLog
import protocol.MemoryReady
import instance.os.Process
import collection.mutable
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConversions._

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
class MemoryActor extends Actor with ActorLogging {

  private val scheduler = context.actorFor("/user/scheduler")

  private var instance: ActorRef = _
  private val blocks: mutable.ConcurrentMap[String, Block] = new ConcurrentHashMap[String, Block]()
  private var capacity: Long = 16000000000L
  private var currentSize: Long = 0
  private var enabled: Boolean = false

  def receive = {
    case MemoryInit(nodeConfig)      ⇒ initialize(nodeConfig)
    case RestartSignal()             ⇒ restart()
    case RequestBlock(process)       ⇒ requestBlock(process)
    case WriteBlockIntoMemory(block) ⇒ writeBlockIntoMemory(block)
    case MemoryStart()               ⇒ startMemory()
  }

  private def initialize(nodeConfig: NodeConfiguration) {
    enabled = true
    instance = sender
    capacity = nodeConfig.getMemory() * Size.GB.getSize
    instance ! MemoryInfoLabel(Size.getSizeString(capacity))
    instance ! MemoryLog(initLog)
    instance ! MemoryReady()
    log.debug("Memory is initialized.")
  }

  private def requestBlock(process: Process) {
    if (enabled) {
      log.debug("Received request for data block %s".format(process.getRequest.getBlockId))
      blocks.get(process.getRequest.getBlockId) match {
        case Some(block) ⇒
          process.setBlockSize(block.getSize)
          instance ! AckBlock(process)

        case None ⇒ instance ! NackBlock(process)
      }
    }
  }

  private def writeBlockIntoMemory(block: Block) {
    if (enabled) {
      blocks.get(block.getName) match {
        case Some(blk) ⇒ log.debug("Block %s is now in memory".format(blk.getName))
        case None      ⇒ loadIntoMemory(block)
      }
    }
  }

  /**
   * Implements LFU (Least Frequently Used) algorithm
   */
  private def loadIntoMemory(block: Block) {
    (currentSize + block.getSize > capacity) match {
      case true ⇒
        val sorted = blocks.values.toList.sortWith(compareBlocks)
        var accumulatedSize = 0L
        val removableBlocks = sorted.takeWhile { b ⇒
          accumulatedSize += b.getSize
          accumulatedSize < block.getSize
        }
        removableBlocks.foreach(b ⇒ blocks.remove(b.getName))
        currentSize -= removableBlocks.map(_.getSize).sum
    }
    block accessed ()
    block.setTimeEnteredInMemory(System.currentTimeMillis())
    blocks.put(block.getName, block)
    currentSize += block.getSize
  }

  private def compareBlocks(a: Block, b: Block): Boolean = {
    if (a.getNrOfAccessedTimes == b.getNrOfAccessedTimes)
      a.getTimeEnteredInMemory.compare(b.getTimeEnteredInMemory) < 0
    else
      a.getNrOfAccessedTimes.compare(b.getNrOfAccessedTimes) < 0
  }

  private def restart() {
    log.debug("Received RESTART signal")
    enabled = false
    blocks.clear()
    currentSize = 0
  }

  private def startMemory() {
    enabled = true
  }

  private def initLog = {
    val sb = new StringBuilder
    sb.append(" Initializing HighMem for node 0 (00000000:00000000)\n")
      .append(" Memory: %s available (2759k kernel code, 13900k reserved, 1287k data, 408k init, 0k highmem)\n".format(Size.getSizeString(capacity)))
      .append(" virtual kernel memory layout:\n")
      .append("     fixmap  : 0xf5716000 - 0xf57ff000   ( 932 kB)\n")
      .append("     pkmap   : 0xf5400000 - 0xf5600000   (2048 kB)\n")
      .append("     vmalloc : 0xe6f00000 - 0xf53fe000   ( 228 MB)\n")
      .append("     lowmem  : 0xc0000000 - 0xe6700000   ( 615 MB)\n")
      .append("       .init : 0xc13f4000 - 0xc145a000   ( 408 kB)\n")
      .append("       .data : 0xc12b1dcf - 0xc13f3cc8   (1287 kB)\n")
      .append("       .text : 0xc1000000 - 0xc12b1dcf   (2759 kB)\n")
    sb.toString()
  }

}
