package cloud.common;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-30
 *
 */

public class RequestEngineTimeout extends Timeout {

	public RequestEngineTimeout(ScheduleTimeout request) {
		super(request);
	}

}
