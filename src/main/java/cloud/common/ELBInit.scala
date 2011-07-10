package cloud.common

import se.sics.kompics.Init
import instance.common.Block
import se.sics.kompics.address.Address
import reflect.BeanProperty

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class ELBInit(@BeanProperty var blocks:java.util.List[Block],
              @BeanProperty var replicationDegree:Int,
              @BeanProperty var self:Address) extends Init