package instance.common

import se.sics.kompics.PortType
import instance.os.RestartSignal
import instance.disk.StartDiskUnit

/**
 * @author Amir Moulavi
 *
 */
class DiskChannel extends PortType {

  positive(classOf[Ready])
  positive(classOf[BlockResponse])
  negative(classOf[LoadBlock])
  negative(classOf[ReadBlock])
  negative(classOf[RestartSignal])
  negative(classOf[StartDiskUnit])

}