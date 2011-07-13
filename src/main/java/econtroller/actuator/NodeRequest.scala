package econtroller.actuator

import se.sics.kompics.Event
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-13
 *
 */
class NodeRequest(val cloudProviderAddress:Address,
                  val controlInput:Double,
                  val numberOfNodes:Int)
  extends Event