package econtroller.controller;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

public class Disconnect extends Message {

	private static final long serialVersionUID = 1419424403990029911L;

	public Disconnect(Address source, Address destination) {
		super(source, destination);
	}


}
