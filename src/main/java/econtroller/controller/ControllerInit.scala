package econtroller.controller

import se.sics.kompics.Init
import econtroller.ControllerConfiguration

/**
 * @author Amir Moulavi
 * @date 2011-07-23
 *
 */
class ControllerInit(val controllerConfiguration:ControllerConfiguration) extends Init {
  def getControllerConfiguration = controllerConfiguration
}