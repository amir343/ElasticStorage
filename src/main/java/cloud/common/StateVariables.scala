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

import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-15
 * 
 */
trait StateVariables {

  @BeanProperty
  var nrNodes:Int = _
  @BeanProperty
  var cpuLoadMean:Double = _
  @BeanProperty
  var cpuLoadSTD:Double = _
  @BeanProperty
	var bandwidthMean:Double = _
  @BeanProperty
	var periodicTotalCost:Double = _
  @BeanProperty
	var totalCost:Double = _
  @BeanProperty
	var responseTimeMean:Double = _
  @BeanProperty
	var throughputMean:Double = _

  def getStringPresentation: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("\n{\n")
      sb.append("\t# nodes: ").append(nrNodes).append("\n")
      sb.append("\tthroughput: ").append(throughputMean).append("\n")
      sb.append("\tcpuMean: ").append(cpuLoadMean).append("\n")
      sb.append("\tcpuSTD: ").append(cpuLoadSTD).append("\n")
      sb.append("\tbandwidth: ").append(bandwidthMean).append("\n")
      sb.append("\tcost: ").append(periodicTotalCost).append("\n")
      sb.append("\tresponseTime: ").append(responseTimeMean).append("\n")
    sb.append("}")
    sb.toString
  }

}