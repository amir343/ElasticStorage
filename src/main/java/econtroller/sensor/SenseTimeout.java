package econtroller.sensor;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class SenseTimeout extends Timeout {

	public SenseTimeout(ScheduleTimeout request) {
		super(request);
	}

}
