package cloud.elb;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-14
 *
 */

public class UpdateELBTree extends Timeout {

	public UpdateELBTree(ScheduleTimeout request) {
		super(request);
	}

}
