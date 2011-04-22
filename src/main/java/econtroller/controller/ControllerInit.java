package econtroller.controller;

import econtroller.ControllerConfiguration;
import se.sics.kompics.Init;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class ControllerInit extends Init {

	private ControllerConfiguration controllerConfiguration;

	public ControllerInit(ControllerConfiguration controllerConfiguration) {
		this.controllerConfiguration = controllerConfiguration;
	}

	public ControllerConfiguration getControllerConfiguration() {
		return controllerConfiguration;
	}

}
