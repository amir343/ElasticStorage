package cloud.api

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-15
 *
 */
class RestartInstance(source:Address, destination:Address) extends Message(source, destination)