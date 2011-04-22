package instance.common;

import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class Ready extends Event {
	
	public enum Device {
		CPU,
		MEMORY,
		DISK
	}

	private Device device;
	
	public Ready(Device device) {
		this.device = device;
	}

	public Device getDevice() {
		return device;
	}
	
}
