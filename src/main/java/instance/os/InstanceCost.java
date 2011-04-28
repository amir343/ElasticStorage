package instance.os;

import instance.Node;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-28
 *
 */

public class InstanceCost extends Message {

	private static final long serialVersionUID = 3300052765351863627L;
	private String cost;
	private Node node;

	public InstanceCost(Address source, Address destination, Node node, String cost) {
		super(source, destination);
		this.node = node;
		this.cost = cost;
	}

	public String getCost() {
		return cost;
	}

	public Node getNode() {
		return node;
	}

}
