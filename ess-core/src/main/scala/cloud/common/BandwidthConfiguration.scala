package cloud.common

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

import instance.MB

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class BandwidthConfiguration(bandwidth: Double) extends Serializable {

  val cost: BandwidthCost = bandwidth match {
    case m if (bandwidth < 1.0) ⇒ XLOW
    case m if (bandwidth >= 1.0 && bandwidth < 2.0) ⇒ LOW
    case m if (bandwidth >= 2.0 && bandwidth < 3.0) ⇒ MEDIUM
    case m if (bandwidth >= 3.0 && bandwidth < 4.0) ⇒ HIGH
    case m if (bandwidth >= 4.0 && bandwidth < 5.0) ⇒ XHIGH
    case _ ⇒ XXHIGH
  }

  def getBandwidthMegaBytePerSecond: Long = (((bandwidth * 10) * MB.size).asInstanceOf[Long]) / 10

  def getBandwidth: Double = bandwidth

  def getCost: BandwidthCost = cost

  override def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("bandwidth: ").append(bandwidth)
    sb.toString
  }
}

sealed trait BandwidthCost { val cost: Double }
case object XLOW extends BandwidthCost { override val cost: Double = 0.10 }
case object LOW extends BandwidthCost { override val cost: Double = 0.15 }
case object MEDIUM extends BandwidthCost { override val cost: Double = 0.20 }
case object HIGH extends BandwidthCost { override val cost: Double = 0.25 }
case object XHIGH extends BandwidthCost { override val cost: Double = 0.30 }
case object XXHIGH extends BandwidthCost { override val cost: Double = 0.35 }

