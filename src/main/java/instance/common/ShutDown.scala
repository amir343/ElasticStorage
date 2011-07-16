package instance.common

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class ShutDown(source:Address, destination:Address) extends Message(source, destination)