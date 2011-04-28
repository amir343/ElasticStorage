package instance.os;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-28
 *
 */

public class CalculateCost extends Timeout {

	public CalculateCost(ScheduleTimeout request) {
		super(request);
	}

}
