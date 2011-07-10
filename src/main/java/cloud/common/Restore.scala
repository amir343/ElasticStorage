package cloud.common

import se.sics.kompics.Event
import se.sics.kompics.address.Address
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class Restore(@BeanProperty var node:Address) extends Event