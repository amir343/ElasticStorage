package econtroller.actuator

import se.sics.kompics.Init
import econtroller.ControllerConfiguration

/**
 * @author Amir Moulavi
 * @date 2011-07-23
 *
 */
class ActuatorInit(val controllerConfiguration:ControllerConfiguration) extends Init {
  def getControllerConfiguration = controllerConfiguration
}