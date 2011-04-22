package econtroller.sensor;

import se.sics.kompics.Init;
import econtroller.ControllerConfiguration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class SensorInit extends Init {

	private ControllerConfiguration controllerConfiguration;

	public SensorInit(ControllerConfiguration controllerConfiguration) {
		this.controllerConfiguration = controllerConfiguration;
	}

	public ControllerConfiguration getControllerConfiguration() {
		return controllerConfiguration;
	}

}
