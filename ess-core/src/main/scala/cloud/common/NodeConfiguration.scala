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

import instance.common.Block
import reflect.BeanProperty
import akka.actor.ActorRef
import scala.collection.mutable

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
case class NodeConfiguration(cpuSpeed: Double = 2.0,
                             bandwidth: Double = 2.0,
                             memory: Int = 4,
                             simultaneousDownloads: Int = 70,
                             headLess: Boolean = false) {

  lazy val cpuConfiguration: CpuConfiguration = new CpuConfiguration(cpuSpeed)
  lazy val bandwidthConfiguration: BandwidthConfiguration = new BandwidthConfiguration(bandwidth)
  lazy val blocks = List.empty[Block]
  lazy val blocksMap = mutable.Map.empty[String, ActorRef]

}