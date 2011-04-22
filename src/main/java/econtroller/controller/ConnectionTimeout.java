package econtroller.controller;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class ConnectionTimeout extends Timeout {

	public ConnectionTimeout(ScheduleTimeout request) {
		super(request);
	}

}
