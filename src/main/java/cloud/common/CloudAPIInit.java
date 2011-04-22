package cloud.common;

import scenarios.manager.CloudConfiguration;
import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class CloudAPIInit extends Init {

	private CloudConfiguration cloudConfiguration;
	private Address self;
	private long period;
	private long delta;

	public CloudAPIInit(CloudConfiguration topology, Address self) {
		this.cloudConfiguration = topology;
		this.self = self;
	}

	public CloudConfiguration getCloudConfiguration() {
		return cloudConfiguration;
	}

	public Address getSelf() {
		return self;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public long getDelta() {
		return delta;
	}

	public void setDelta(long delta) {
		this.delta = delta;
	}

}
