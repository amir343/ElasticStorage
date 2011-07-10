package cloud.common

import se.sics.kompics.Event
import instance.Node
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class RestoreNode(@BeanProperty var node:Node) extends Event