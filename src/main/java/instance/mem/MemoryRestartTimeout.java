package instance.mem;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-15
 *
 */

public class MemoryRestartTimeout extends Timeout {

	public MemoryRestartTimeout(ScheduleTimeout request) {
		super(request);
	}

}
