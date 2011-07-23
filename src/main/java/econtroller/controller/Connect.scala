package econtroller.controller

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-23
 *
 */
class Connect(source:Address, destination:Address) extends Message(source, destination)