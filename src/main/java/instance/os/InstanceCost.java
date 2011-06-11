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
	private String totalCost;
	private Node node;
	private String periodicCost;

	public InstanceCost(Address source, Address destination, Node node, String totalCost, String periodicCost) {
		super(source, destination);
		this.node = node;
		this.totalCost = totalCost;
		this.periodicCost = periodicCost; 
	}

	public String getTotalCost() {
		return totalCost;
	}

	public String getPeriodicCost() {
		return periodicCost;
	}

	public Node getNode() {
		return node;
	}

}
