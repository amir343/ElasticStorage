package cloud.common

import se.sics.kompics.address.Address
import se.sics.kompics.Event
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 **/
class Suspect(@BeanProperty var node:Address) extends Event