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
package cloud.common

import instance.common.Size

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class CpuConfiguration(cpuSpeed: Double) extends Serializable {

  val cost: CPUCost = cpuSpeed match {
    case m if cpuSpeed < 1.8                      ⇒ CPUCost.MICRO
    case m if (cpuSpeed >= 1.8 && cpuSpeed < 2.0) ⇒ CPUCost.XSMALL
    case m if (cpuSpeed >= 2.0 && cpuSpeed < 2.4) ⇒ CPUCost.SMALL
    case m if (cpuSpeed >= 2.4 && cpuSpeed < 2.8) ⇒ CPUCost.LARGE
    case m if (cpuSpeed >= 2.8 && cpuSpeed < 3.2) ⇒ CPUCost.XLARGE
    case m if (cpuSpeed >= 3.2 && cpuSpeed < 3.8) ⇒ CPUCost.XXLARGE
    case _                                        ⇒ CPUCost.XXXLARGE
  }

  def getCost: CPUCost = cost

  def getCpuSpeedInstructionPerSecond: Long = (((cpuSpeed * 10) * Size.GHertz.getSize).asInstanceOf[Long]) / 10

  def getCpuSpeed: Double = cpuSpeed

  override def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("cpu: ").append(cpuSpeed)
    sb.toString
  }
}