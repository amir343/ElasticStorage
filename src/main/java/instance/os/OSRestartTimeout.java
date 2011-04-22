package instance.os;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-15
 *
 */

public class OSRestartTimeout extends Timeout {

	public OSRestartTimeout(ScheduleTimeout request) {
		super(request);
	}

}
