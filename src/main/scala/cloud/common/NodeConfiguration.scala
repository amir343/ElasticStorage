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

import instance.Node
import se.sics.kompics.address.Address
import instance.common.Block
import reflect.BeanProperty
import akka.actor.ActorRef
import scala.collection.mutable

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class NodeConfiguration(cpuSpeed: Double,
                        bandwidth: Double,
                        @BeanProperty var memory: Int,
                        @BeanProperty var simultaneousDownloads: Int)
  extends Serializable {

  def this() = this(2.0, 2.0, 4, 70)

  @BeanProperty
  var cpuConfiguration: CpuConfiguration = new CpuConfiguration(cpuSpeed)
  @BeanProperty
  var bandwidthConfiguration: BandwidthConfiguration = new BandwidthConfiguration(bandwidth)
  @BeanProperty
  var name: String = "NONAME"
  @BeanProperty
  var node: Node = null
  @BeanProperty
  var blocks = List.empty[Block]
  @Deprecated
  @BeanProperty
  var dataBlocksMap: java.util.Map[String, Address] = null
  @BeanProperty
  var blocksMap = mutable.Map.empty[String, ActorRef]
  @BeanProperty
  var headLess: Boolean = false

  def setNodeInfo(node: Node) {
    this.node = node
    this.name = node.getNodeName
  }

  def cpu(speed: Double): NodeConfiguration = {
    this.cpuConfiguration = new CpuConfiguration(speed)
    this
  }

  def memoryGB(size: Int): NodeConfiguration = {
    this.memory = size
    this
  }

  def bandwidthMB(bandwidth: Double): NodeConfiguration = {
    this.bandwidthConfiguration = new BandwidthConfiguration(bandwidth)
    this
  }

  override def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("{")
    sb.append("cpuConfig").append(cpuConfiguration).append(",")
    sb.append("memory: ").append(memory).append(",")
    sb.append("bandwidth").append(bandwidthConfiguration).append(",")
    sb.append("simDownloads: ").append(simultaneousDownloads)
    sb.append("}")
    sb.toString
  }
}