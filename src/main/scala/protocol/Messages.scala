package protocol

import cloud.common.NodeConfiguration
import instance.os.Process
import instance.common.{ AbstractOperation => AOperation }
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

// Instance Messages
case class InstanceStart(nodeConfiguration:NodeConfiguration) extends Messages

// CPU Messages
case class CPUInit(nodeConfiguration:NodeConfiguration) extends Messages
case class CPULog(log:String) extends Messages
case class CPUReady() extends Messages
case class LoadSamplerTimeout() extends Messages
case class LoadCalculationTimeout() extends Messages
case class CPULoad(load:Double) extends Messages
case class RestartSignal() extends Messages
case class Restart() extends Messages
case class StartProcess(process:Process) extends Messages
case class EndProcess(process:Process) extends Messages
case class AbstractOperation(operation:AOperation) extends Messages
case class OperationFinishedTimeout(pid:String) extends Messages
case class SnapshotRequest(chart:JFreeChart = null) extends Messages
