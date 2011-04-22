package econtroller.sensor;

import instance.os.MonitorResponse;
import econtroller.controller.Sense;
import se.sics.kompics.PortType;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class SensorChannel extends PortType {
	{
		negative(Sense.class);
		positive(MonitorResponse.class);
	}
}
