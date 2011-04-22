package cloud.common;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-27
 *
 */

public class Suspect extends Event {

	private Address node;

	public Suspect(Address node) {
		this.node = node;
	}

	public Address getNode() {
		return node;
	}

}
