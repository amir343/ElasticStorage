package cloud.common

import se.sics.kompics.Event
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class GetReplicas(@BeanProperty var nodeConfiguration:NodeConfiguration) extends Event