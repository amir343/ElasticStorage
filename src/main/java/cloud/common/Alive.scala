package cloud.common

import se.sics.kompics.address.Address
import se.sics.kompics.network.Message
/**
 * @author Amir Moulavi
 * @date 2011-07-09
 */

class Alive(source:Address, destination:Address) extends Message(source, destination)