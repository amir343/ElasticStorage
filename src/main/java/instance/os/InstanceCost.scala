package instance.os

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import instance.Node
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class InstanceCost(source:Address,
                   destination:Address,
                   @BeanProperty var node:Node,
                   @BeanProperty var totalCost:String,
                   @BeanProperty var periodicCost:String)
  extends Message(source, destination)