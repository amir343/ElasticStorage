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

import instance.GHertz

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class CpuConfiguration(cpuSpeed: Double) extends Serializable {

  val cost: CPUCost = cpuSpeed match {
    case m if cpuSpeed < 1.8                      ⇒ MICRO
    case m if (cpuSpeed >= 1.8 && cpuSpeed < 2.0) ⇒ XSMALL
    case m if (cpuSpeed >= 2.0 && cpuSpeed < 2.4) ⇒ SMALL
    case m if (cpuSpeed >= 2.4 && cpuSpeed < 2.8) ⇒ LARGE
    case m if (cpuSpeed >= 2.8 && cpuSpeed < 3.2) ⇒ XLARGE
    case m if (cpuSpeed >= 3.2 && cpuSpeed < 3.8) ⇒ XXLARGE
    case _                                        ⇒ XXXLARGE
  }

  def getCost: CPUCost = cost

  def getCpuSpeedInstructionPerSecond: Long = (((cpuSpeed * 10) * GHertz.size).asInstanceOf[Long]) / 10

  def getCpuSpeed: Double = cpuSpeed

  override def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("cpu: ").append(cpuSpeed)
    sb.toString
  }
}

sealed trait CPUCost {
  val perhour: Double
}
case object MICRO extends CPUCost { override val perhour: Double = 0.025 }
case object XSMALL extends CPUCost { override val perhour: Double = 0.056 }
case object SMALL extends CPUCost { override val perhour: Double = 0.095 }
case object LARGE extends CPUCost { override val perhour: Double = 0.38 }
case object XLARGE extends CPUCost { override val perhour: Double = 0.76 }
case object XXLARGE extends CPUCost { override val perhour: Double = 1.14 }
case object XXXLARGE extends CPUCost { override val perhour: Double = 2.28 }
