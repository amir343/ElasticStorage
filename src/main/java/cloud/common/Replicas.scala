package cloud.common

import se.sics.kompics.Event
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-09-07
 *
 */
class Replicas(@BeanProperty var nodeConfiguration:NodeConfiguration) extends Event