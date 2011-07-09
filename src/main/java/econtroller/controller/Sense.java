package econtroller.controller;

import java.util.List;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class Sense extends Event {

	private Address cloudProvider;

	public Sense(Address cloudProviderAddress) {
		this.cloudProvider = cloudProviderAddress;
	}

	public Address getCloudProvider() {
		return cloudProvider;
	}
	
}
