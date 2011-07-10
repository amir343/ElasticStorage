package cloud.common

import se.sics.kompics.PortType
import cloud.epfd.HealthCheckerInit

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class EPFD extends PortType {

  positive(classOf[Suspect])
  positive(classOf[Restore])
  negative(classOf[HealthCheckerInit])
  negative(classOf[ConsiderInstance])
  negative(classOf[InstanceKilled])

}