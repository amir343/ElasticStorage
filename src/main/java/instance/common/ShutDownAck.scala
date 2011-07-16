package instance.common

import se.sics.kompics.network.Message
import instance.Node
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 *
 */
class ShutDownAck(source:Address, destination:Address, node:Node) extends Message(source, destination) {

  def getNode = node

}