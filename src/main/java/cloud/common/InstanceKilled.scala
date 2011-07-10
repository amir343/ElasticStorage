package cloud.common

import se.sics.kompics.Event
import instance.Node
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 */
class InstanceKilled(val node:Node) extends Event {
  def getAddress:Address = node.getAddress
}