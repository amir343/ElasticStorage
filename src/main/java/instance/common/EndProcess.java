package instance.common;

import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class EndProcess extends Event {

	private String pid;

	public EndProcess(String pid) {
		this.pid = pid;
	}

	public String getPid() {
		return pid;
	}
	
}
