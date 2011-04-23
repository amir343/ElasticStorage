package econtroller.actuator;

import econtroller.ControllerConfiguration;
import se.sics.kompics.Init;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-23
 *
 */

public class ActuatorInit extends Init {

	private ControllerConfiguration controllerConfiguration;

	public ActuatorInit(ControllerConfiguration controllerConfiguration) {
		this.controllerConfiguration = controllerConfiguration;
	}

	public ControllerConfiguration getControllerConfiguration() {
		return controllerConfiguration;
	}

}
