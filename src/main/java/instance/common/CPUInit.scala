package instance.common

import se.sics.kompics.Init
import cloud.common.NodeConfiguration

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */

class CPUInit(nodeConfiguration:NodeConfiguration) extends Init {

  def getNodeConfiguration = nodeConfiguration

}