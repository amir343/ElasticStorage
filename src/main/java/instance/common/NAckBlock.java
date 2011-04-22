package instance.common;

import instance.os.Process;
import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class NAckBlock extends Event {

	private Process process;

	public NAckBlock(Process process) {
		this.process = process;
	}

	public Process getProcess() {
		return process;
	}

}
