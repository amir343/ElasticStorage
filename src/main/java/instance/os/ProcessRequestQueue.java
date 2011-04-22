package instance.os;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-03
 *
 */

public class ProcessRequestQueue extends Timeout {

	public ProcessRequestQueue(ScheduleTimeout request) {
		super(request);
	}

}
