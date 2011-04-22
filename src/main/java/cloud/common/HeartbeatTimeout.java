package cloud.common;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-27
 *
 */

public class HeartbeatTimeout extends Timeout {

	public HeartbeatTimeout(ScheduleTimeout request) {
		super(request);
	}

}
