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
class OSInit(instanceConfiguration:InstanceConfiguration) extends Init {

  def getBlocks: JList[Block] = instanceConfiguration.getBlocks

  def getCloudProviderAddress: Address = instanceConfiguration.getCloudProviderAddress

  def getSelfAddress: Address = instanceConfiguration.getSelfAddress

  def getNodeInfo: Node = instanceConfiguration.getNode

  def getNodeConfiguration: NodeConfiguration = instanceConfiguration.getNodeConfiguration

}