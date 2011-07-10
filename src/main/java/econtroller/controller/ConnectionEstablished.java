package econtroller.controller;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class ConnectionEstablished extends Message {

	private static final long serialVersionUID = -6400484430819816071L;
	private List<Address> nodes;

	public ConnectionEstablished(Address source, Address destination, List<Address> nodes) {
		super(source, destination);
		this.nodes = nodes;
	}

	public List<Address> getNodes() {
		return nodes;
	}

}
