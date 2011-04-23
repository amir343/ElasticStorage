package econtroller.actuator;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-23
 *
 */

public class NodeRequest extends Event {

	private Address cloudProviderAddress;

	public NodeRequest(Address cloudProviderAddress) {
		this.cloudProviderAddress = cloudProviderAddress;
	}

	public Address getCloudProviderAddress() {
		return cloudProviderAddress;
	}

}
