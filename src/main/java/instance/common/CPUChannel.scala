package instance.common

import instance.os.{RestartSignal, SnapshotRequest, CPULoad}
import se.sics.kompics.PortType

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class CPUChannel extends PortType {

  positive(classOf[Ready])
  positive(classOf[CPULoad])
  positive(classOf[SnapshotRequest])
  negative(classOf[StartProcess])
  negative(classOf[EndProcess])
  negative(classOf[AbstractOperation])
  negative(classOf[RestartSignal])
  negative(classOf[SnapshotRequest])

}