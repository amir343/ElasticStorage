package instance.common;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class LoadCalculationTimeout extends Timeout {

	public LoadCalculationTimeout(ScheduleTimeout request) {
		super(request);
	}

}
