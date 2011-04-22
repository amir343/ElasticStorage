package instance.os;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-04
 *
 */

public class PropagateCPULoad extends Timeout {

	public PropagateCPULoad(ScheduleTimeout request) {
		super(request);
	}

}
