package instance.common

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import instance.Node

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class InstanceStarted(source:Address, destination:Address, node:Node) extends Message(source, destination) {
  def getNode = node
}