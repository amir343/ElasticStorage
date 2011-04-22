package instance.common;

import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class ReadBlock extends Event {
	
	private String id;
	private instance.os.Process process;
	
	public ReadBlock(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setProcess(instance.os.Process process) {
		this.process = process;
	}
	
	public instance.os.Process getProcess() {
		return process;
	}
	
}
