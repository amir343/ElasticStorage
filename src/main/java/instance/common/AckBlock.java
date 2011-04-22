package instance.common;

import instance.os.Process;
import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class AckBlock extends Event {

	private Process process;

	public AckBlock(Process process) {
		this.process = process;
	}

	public Process getProcess() {
		return process;
	}

}
