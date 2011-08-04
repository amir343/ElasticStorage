package instance.common

import se.sics.kompics.address.Address
import se.sics.kompics.network.Message

/**
 * @author Amir Moulavi
 * @date 2011-08-04
 */
class CloseMyStream(self: Address, destination: Address) extends Message(self, destination)