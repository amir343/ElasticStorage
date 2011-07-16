package instance.os

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import instance.common.Block
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class RebalanceResponse(source:Address,
                        destination:Address,
                        @BeanProperty var block:Block)
  extends Message(source, destination)