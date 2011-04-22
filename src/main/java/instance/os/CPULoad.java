package instance.os;

import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-04
 *
 */

public class CPULoad extends Event {
	
	private double cpuLoad;

	public CPULoad(double cpuLoad) {
		this.cpuLoad = cpuLoad;
	}

	public double getCpuLoad() {
		return cpuLoad;
	}

}
