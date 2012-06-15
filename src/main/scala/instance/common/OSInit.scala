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
package instance.common

import se.sics.kompics.Init
import cloud.api.InstanceConfiguration
import se.sics.kompics.address.Address
import instance.Node
import cloud.common.NodeConfiguration
import java.util.{List => JList}

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class OSInit(instanceConfiguration: InstanceConfiguration) extends Init {

  def getBlocks: JList[Block] = instanceConfiguration.getBlocks

  def getCloudProviderAddress: Address = instanceConfiguration.getCloudProviderAddress

  def getSelfAddress: Address = instanceConfiguration.getSelfAddress

  def getNodeInfo: Node = instanceConfiguration.getNode

  def getNodeConfiguration: NodeConfiguration = instanceConfiguration.getNodeConfiguration

}