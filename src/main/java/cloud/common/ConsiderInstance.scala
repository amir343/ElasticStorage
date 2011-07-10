package cloud.common

import instance.Node
import se.sics.kompics.Event
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class ConsiderInstance(node:Node) extends Event {

  def getAddress:Address = node.getAddress

  def getNode:String = node.toString

}