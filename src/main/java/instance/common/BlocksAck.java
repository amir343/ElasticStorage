package instance.common;

import scenarios.manager.Cloud.Node;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-30
 *
 */

public class BlocksAck extends Message {

	private static final long serialVersionUID = -4273797576275734879L;
	private Node node;

	public BlocksAck(Address source, Address destination, Node node) {
		super(source, destination);
		this.node = node;
	}

	public Node getNode() {
		return node;
	}
	
}
