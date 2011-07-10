package cloud.elb

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import instance.Node
import instance.common.Block

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class ActivateBlock(source:Address, destination:Address, val node:Node, val block:Block) extends Message(source, destination)