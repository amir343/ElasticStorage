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

import se.sics.kompics.Event
import se.sics.kompics.address.Address
import scala.reflect.BeanProperty
import scenarios.manager.SLA

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class SendRawData(val controller: Address,
                  val numberOfNodes: Int,
                  val trainingData: Boolean) extends Event {

  @BeanProperty
  var periodicTotalCost: Double = _
  @BeanProperty
  var totalCost: Double = _
  @BeanProperty
  var averageThroughput: Double = _
  @BeanProperty
  var averageResponseTime: Double = _

  var rt: List[Long] = List[Long]()

  def addResponseTime(responseTime: Long) {
    rt = rt ::: List(responseTime)
  }

  def updateSLA(sla: SLA) {
    for (element <- rt)
      sla.addResponseTime(element)
  }

}