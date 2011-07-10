package econtroller.sensor;

import econtroller.ControllerConfiguration;
import se.sics.kompics.Init;

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
