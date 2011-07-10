package cloud.common

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import instance.common.Request
/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class RequestMessage(source:Address, destination:Address, val request:Request) extends Message(source, destination)