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

	private List<Address> nodes;

	public Sense(List<Address> nodes) {
		this.nodes = nodes;
	}

	public List<Address> getNodes() {
		return nodes;
	}

}
