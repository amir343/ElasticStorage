package cloud.elb;

import instance.Node;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04
 *
 */

public class MyCPULoadAndBandwidth extends Message {

	private static final long serialVersionUID = -2182498572620179460L;
	private Node node;
	private double cpuLoad;
	private long currentBandwidth;

	public MyCPULoadAndBandwidth(Address source, Address destination, Node node, double cpuLoad, long currentBandwidth) {
		super(source, destination);
		this.node = node;	
		this.cpuLoad = cpuLoad;
		this.currentBandwidth = currentBandwidth;
	}

	public Node getNode() {
		return node;
	}

	public double getCpuLoad() {
		return this.cpuLoad;
	}

	public long getCurrentBandwidth() {
		return currentBandwidth;
	}

}
