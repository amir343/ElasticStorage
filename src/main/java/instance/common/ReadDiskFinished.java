package instance.common;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class ReadDiskFinished extends Timeout {

	private String pid;

	public ReadDiskFinished(ScheduleTimeout request) {
		super(request);
		// TODO Auto-generated constructor stub
	}

	public void setPid(String pid) {
		this.pid = pid;		
	}

	public String getPid() {
		return pid;
	}

}
