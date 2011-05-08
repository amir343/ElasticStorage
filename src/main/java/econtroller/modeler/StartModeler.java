package econtroller.modeler;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class StartModeler extends Event {

	private Address cloudProvider;

	public StartModeler(Address cloudProviderAddress) {
		this.cloudProvider = cloudProviderAddress;
	}
	
	public Address getCloudProviderAddress() {
		return cloudProvider;
	}

}
