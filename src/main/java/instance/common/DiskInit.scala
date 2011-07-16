package instance.common

import se.sics.kompics.Init
import cloud.common.NodeConfiguration

/**
 * @author Amir Moulavi
 * @dater 2011-07-16
 *
 */
class DiskInit(nodeConfiguration:NodeConfiguration) extends Init {

  def getNodeConfiguration = nodeConfiguration

}