package instance.common;

import instance.Node;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-26
 *
 */

public class ShutDownAck extends Message {

	private static final long serialVersionUID = -8181475298565925584L;
	private Node node;

	public ShutDownAck(Address source, Address destination, Node node) {
		super(source, destination);
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

}
