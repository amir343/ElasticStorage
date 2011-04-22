package econtroller.controller;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class Connect extends Message {

	private static final long serialVersionUID = 3288063047866628180L;

	public Connect(Address source, Address destination) {
		super(source, destination);
	}

	
}
