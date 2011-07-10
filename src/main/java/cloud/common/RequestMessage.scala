package cloud.common

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import instance.common.Request
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class RequestMessage(source:Address, destination:Address, @BeanProperty var request:Request) extends Message(source, destination)