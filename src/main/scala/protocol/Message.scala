package protocol

import cloud.common.NodeConfiguration
import instance.os.Process
import instance.common.{ AbstractOperation ⇒ AOperation, Request, Block }
import org.jfree.chart.JFreeChart
import akka.actor.ActorRef

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
trait Message

case class Schedule(delay: Long, actor: ActorRef, message: Message) extends Message

trait InstanceMessage extends Message
// Instance Messages
case class InstanceStart(nodeConfiguration: NodeConfiguration) extends InstanceMessage
case class RestartSignal() extends InstanceMessage
case class BlocksAck() extends InstanceMessage
case class RebalanceRequest(blockId: String) extends InstanceMessage
case class InstanceStarted() extends InstanceMessage
case class WaitTimeout() extends InstanceMessage
case class ProcessRequestQueue() extends InstanceMessage
case class Rejected(req: Request) extends InstanceMessage

// CPU Messages
case class CPUInit(nodeConfiguration: NodeConfiguration) extends InstanceMessage
case class CPULog(log: String) extends InstanceMessage
case class CPUReady() extends InstanceMessage
case class LoadSamplerTimeout() extends InstanceMessage
case class LoadCalculationTimeout() extends InstanceMessage
case class CPULoad(load: Double) extends InstanceMessage
case class Restart() extends InstanceMessage
case class StartProcess(process: Process) extends InstanceMessage
case class EndProcess(process: Process) extends InstanceMessage
case class AbstractOperation(operation: AOperation) extends InstanceMessage
case class OperationFinishedTimeout(pid: String) extends InstanceMessage
case class SnapshotRequest(chart: JFreeChart = null) extends InstanceMessage
case class UpdateCPUInfoLabel(label: String) extends InstanceMessage
case class CPULoadDiagram(chart: JFreeChart) extends InstanceMessage

// Disk Messages
case class DiskInit(nodeConfig: NodeConfiguration) extends InstanceMessage
case class DiskReady() extends InstanceMessage
case class LoadBlock(blocks: List[Block]) extends InstanceMessage
case class ReadBlock(id: String, process: Process) extends InstanceMessage
case class BlockResponse(block: Block, process: Process) extends InstanceMessage

// Memory Messages
case class MemoryInit(nodeConfig: NodeConfiguration) extends InstanceMessage
case class MemoryInfoLabel(label: String) extends InstanceMessage
case class MemoryLog(msg: String) extends InstanceMessage
case class MemoryReady() extends InstanceMessage
case class RequestBlock(process: Process) extends InstanceMessage
case class AckBlock(process: Process) extends InstanceMessage
case class NackBlock(process: Process) extends InstanceMessage
case class WriteBlockIntoMemory(block: Block) extends InstanceMessage
case class MemoryStart() extends InstanceMessage

// Kernel
case class KernelInit(cpuSpeed: Long) extends InstanceMessage
case class KernelLog(msg: String) extends InstanceMessage
