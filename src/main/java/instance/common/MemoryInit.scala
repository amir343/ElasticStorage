package instance.common

import se.sics.kompics.Init
import cloud.common.NodeConfiguration

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class MemoryInit(nodeConfiguration:NodeConfiguration) extends Init {
  def getNodeConfiguration = nodeConfiguration
}