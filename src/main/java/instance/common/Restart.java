package instance.common;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-15
 *
 */

public class Restart extends Timeout {

	public Restart(ScheduleTimeout request) {
		super(request);
	}

}
