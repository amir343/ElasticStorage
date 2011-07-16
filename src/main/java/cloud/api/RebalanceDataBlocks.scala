package cloud.api

import se.sics.kompics.Event
import cloud.common.NodeConfiguration
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class RebalanceDataBlocks(@BeanProperty var nodeConfiguration:NodeConfiguration) extends Event