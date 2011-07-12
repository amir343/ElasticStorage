package cloud.elb

import se.sics.kompics.address.Address
import instance.Node
import se.sics.kompics.network.Message

/**
 * @author Amir Moulavi
 * @date 2011-07-12
 *
 */
class MyCPULoadAndBandwidth(source:Address,
                            destination:Address,
                            val node:Node,
                            val cpuLoad:Double,
                            val currentBandwidth:Long)
  extends Message(source, destination)