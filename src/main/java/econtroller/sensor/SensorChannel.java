package econtroller.sensor;

import se.sics.kompics.PortType;
import cloud.elb.SenseData;
import econtroller.controller.Sense;
import econtroller.controller.StartSense;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class SensorChannel extends PortType {
	{
		negative(Sense.class);
		negative(StartSense.class);
		negative(StopSense.class);
		positive(SenseData.class);
	}
}
