package econtroller.controller

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 *
 */
class Disconnect(source:Address, destination:Address) extends Message(source, destination)