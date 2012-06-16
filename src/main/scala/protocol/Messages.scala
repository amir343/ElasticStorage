package protocol

import cloud.common.NodeConfiguration
import instance.os.Process
import instance.common.{AbstractOperation => AOperation, Block}
import org.jfree.chart.JFreeChart


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
trait Messages


trait InstanceMessages extends Messages
// Instance Messages
case class InstanceStart(nodeConfiguration:NodeConfiguration) extends InstanceMessages
case class RestartSignal() extends InstanceMessages

// CPU Messages
case class CPUInit(nodeConfiguration:NodeConfiguration) extends InstanceMessages
case class CPULog(log:String) extends InstanceMessages
case class CPUReady() extends InstanceMessages
case class LoadSamplerTimeout() extends InstanceMessages
case class LoadCalculationTimeout() extends InstanceMessages
case class CPULoad(load:Double) extends InstanceMessages
case class Restart() extends InstanceMessages
case class StartProcess(process:Process) extends InstanceMessages
case class EndProcess(process:Process) extends InstanceMessages
case class AbstractOperation(operation:AOperation) extends InstanceMessages
case class OperationFinishedTimeout(pid:String) extends InstanceMessages
case class SnapshotRequest(chart:JFreeChart = null) extends InstanceMessages
case class UpdateCPUInfoLabel(label:String) extends InstanceMessages
case class CPULoadDiagram(chart:JFreeChart) extends InstanceMessages

// Disk Messages
case class DiskInit(nodeConfig:NodeConfiguration) extends InstanceMessages
case class DiskReady() extends InstanceMessages
case class LoadBlock(blocks:List[Block]) extends InstanceMessages
case class ReadBlock(id:String, process:Process) extends InstanceMessages
case class BlockResponse(block:Block, process:Process) extends InstanceMessages
