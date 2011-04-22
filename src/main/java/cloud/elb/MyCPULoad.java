package cloud.elb;

import scenarios.manager.Cloud.Node;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04
 *
 */

public class MyCPULoad extends Message {

	private static final long serialVersionUID = -2182498572620179460L;
	private Node node;
	private double cpuLoad;

	public MyCPULoad(Address source, Address destination, Node node, double cpuLoad) {
		super(source, destination);
		this.node = node;	
		this.cpuLoad = cpuLoad;
	}

	public Node getNode() {
		return node;
	}

	public double getCpuLoad() {
		return this.cpuLoad;
	}

}
