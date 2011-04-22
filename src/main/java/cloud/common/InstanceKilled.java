package cloud.common;

import scenarios.manager.Cloud.Node;
import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-27
 *
 */

public class InstanceKilled extends Event {

	private Node node;

	public InstanceKilled(Node node) {
		this.node = node;
	}

	public Address getAddress() {
		return node.getAddress();
	}

	public String getNode() {
		return node.toString();
	}
	
}
