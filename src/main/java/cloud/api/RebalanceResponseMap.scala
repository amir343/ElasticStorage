package cloud.api

import cloud.common.NodeConfiguration
import se.sics.kompics.Event
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class RebalanceResponseMap(@BeanProperty var nodeConfiguration:NodeConfiguration) extends Event