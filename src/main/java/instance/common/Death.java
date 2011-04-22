package instance.common;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-26
 *
 */

public class Death extends Timeout {

	public Death(ScheduleTimeout request) {
		super(request);
	}

}
