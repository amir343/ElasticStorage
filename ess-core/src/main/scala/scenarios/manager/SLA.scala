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
package scenarios.manager

import scala.Double
import scala.collection.mutable

/**
 * @author Amir Moulavi
 * @date 2011-08-12
 */

case class SLA(cpuLoad: Double = 55.0, responseTime: Long = 1500, bandwidth: Double = 200000.0) {

  private var cpuLoadList = mutable.ListBuffer[Double]()
  private var rtList = mutable.ListBuffer[Long]()
  private var bandwidthList = mutable.ListBuffer[Double]()

  def addCpuLoad(load: Double) = cpuLoadList += load

  def addBandwidth(band: Double) = bandwidthList += band

  def addResponseTime(rt: Long) = rtList += rt

  def cpuLoadViolation: Double = 100.0 * cpuLoadList.filter(_ >= cpuLoad).size.asInstanceOf[Double] / cpuLoadList.size.asInstanceOf[Double]

  def responseTimeViolation: Double = 100.0 * rtList.filter(_ >= responseTime).size.asInstanceOf[Double] / rtList.size.asInstanceOf[Double]

  def bandwidthViolation: Double = 100.0 * bandwidthList.filter(_ <= bandwidth).size.asInstanceOf[Double] / bandwidthList.size.asInstanceOf[Double]

}