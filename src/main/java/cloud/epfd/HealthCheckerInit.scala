package cloud.epfd

import se.sics.kompics.Init
import se.sics.kompics.address.Address

/**
 * @author Amir Moulavi
 * @date 2011-07-12
 *
 */
class HealthCheckerInit(val period:Long, val delta:Long, val self:Address) extends Init