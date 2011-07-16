package instance.os

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class RebalanceRequest(source:Address,
                       destination:Address,
                       @BeanProperty var blockId:String )
  extends Message(source, destination)