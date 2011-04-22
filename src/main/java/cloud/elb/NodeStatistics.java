package cloud.elb;

import scenarios.manager.Cloud.Node;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-04
 *
 */

public class NodeStatistics {

	private double cpuLoad = 0.0;
	private int nrOfSentRequest = 0;
	private Node node;
	
	public NodeStatistics(Node node) {
		this.node = node;
	}

	public double getCpuLoad() {
		return cpuLoad;
	}

	public void setCpuLoad(double cpuLoad) {
		this.cpuLoad = cpuLoad;
	}
	
	public void increaseNrOfSentRequest() {
		nrOfSentRequest++;
	}

	public int getNrOfSentRequest() {
		return nrOfSentRequest;
	}

	public Node getNode() {
		return node;
	}
	
}
