package cloud.epfd;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-27
 *
 */

public class HealthCheckerInit extends Init {

	
	private long period;
	private long delta;
	private Address self;


	public HealthCheckerInit(long period, long delta, Address self) {
		this.period = period;
		this.delta = delta;
		this.self = self;
	}
	
	public long getPeriod() {
		return period;
	}

	public long getDelta() {
		return delta;
	}

	public Address getSelfAddress() {
		return self;
	}

}
