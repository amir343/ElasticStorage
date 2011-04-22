package cloud.requestengine;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public class RTCollectionTimeout extends Timeout {

	public RTCollectionTimeout(ScheduleTimeout request) {
		super(request);
	}

}
