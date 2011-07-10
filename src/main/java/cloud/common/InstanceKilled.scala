package cloud.common

import se.sics.kompics.Event
import instance.Node
import reflect.BeanProperty
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 */
class InstanceKilled(@BeanProperty var node:Node) extends Event {
  def getAddress:Address = node.getAddress
}