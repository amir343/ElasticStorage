/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cloud.common

import instance.common.Size

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class BandwidthConfiguration(bandwidth:Double) extends Serializable {

  val cost:BandwidthCost = bandwidth match {
    case m if (bandwidth < 1.0) => BandwidthCost.XLOW
    case m if (bandwidth >= 1.0 && bandwidth < 2.0) => BandwidthCost.LOW
    case m if (bandwidth >= 2.0 && bandwidth < 3.0) => BandwidthCost.MEDIUM
    case m if (bandwidth >= 3.0 && bandwidth < 4.0) => BandwidthCost.HIGH
    case m if (bandwidth >= 4.0 && bandwidth < 5.0) => BandwidthCost.XHIGH
    case _ => BandwidthCost.XXHIGH
  }

  def getBandwidthMegaBytePerSecond: Long = (((bandwidth * 10) * Size.MB.getSize).asInstanceOf[Long]) / 10

  def getBandwidth: Double = bandwidth

  def getCost: BandwidthCost = cost

  override def toString: String = {
      val sb: StringBuilder = new StringBuilder
      sb.append("bandwidth: ").append(bandwidth)
      sb.toString
  }
}