package econtroller.controller;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-19
 *
 */

public class ActuateTimeout extends Timeout {

	public ActuateTimeout(ScheduleTimeout request) {
		super(request);
	}

}
