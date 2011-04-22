package instance.common;

import instance.os.Process;
import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class StartProcess extends Event {
	
	private Process process;

	public StartProcess(Process p) {
		this.process = p;
	}

	public Process getProcess() {
		return process;
	}

}
