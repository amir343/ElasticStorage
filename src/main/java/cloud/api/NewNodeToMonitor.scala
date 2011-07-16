package cloud.api

import se.sics.kompics.network.Message
import se.sics.kompics.address.Address
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class NewNodeToMonitor(source:Address,
                       destination:Address,
                       @BeanProperty var newNode:Address)
  extends Message(source, destination)