package econtroller.sensor

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-23
 *
 */
class Monitor(source:Address, destination:Address) extends Message(source, destination)