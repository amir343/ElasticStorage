package econtroller.controller

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-13
 *
 */
class NewNodeRequest(source:Address, destination:Address, val numberOfNodes:Int) extends Message(source, destination)