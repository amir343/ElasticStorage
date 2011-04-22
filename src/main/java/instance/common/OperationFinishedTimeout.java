package instance.common;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-20
 *
 */

public class OperationFinishedTimeout extends Timeout {

	private String pid;

	public OperationFinishedTimeout(ScheduleTimeout request) {
		super(request);
	}

	public void setPid(String pid) {
		this.pid = pid;		
	}

	public String getPid() {
		return pid;
	}

}
