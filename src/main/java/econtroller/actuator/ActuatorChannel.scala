package econtroller.actuator

import se.sics.kompics.PortType

/**
 * @author Amir Moulavi
 * @date 2011-07-23
 *
 */
class ActuatorChannel extends PortType {
  negative(classOf[NodeRequest])
}