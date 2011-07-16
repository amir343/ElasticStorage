package instance.common

import se.sics.kompics.PortType
import instance.os.RestartSignal
import instance.mem.StartMemoryUnit

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class MemChannel extends PortType {

  positive(classOf[Ready])
  positive(classOf[AckBlock])
  positive(classOf[NAckBlock])
  negative(classOf[RequestBlock])
  negative(classOf[WriteBlockIntoMemory])
  negative(classOf[RestartSignal])
  negative(classOf[StartMemoryUnit])

}