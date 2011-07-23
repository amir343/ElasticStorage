package econtroller.controller

import se.sics.kompics.Event
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-23
 *
 */
class Sense(val cloudProvider:Address) extends Event {
  def getCloudProvider = cloudProvider
}