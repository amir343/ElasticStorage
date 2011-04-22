package cloud.common;

import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-28
 *
 */

public class Replicas extends Event {

	private NodeConfiguration nodeConfiguration;

	public Replicas(NodeConfiguration nodeConfiguration) {
		this.nodeConfiguration = nodeConfiguration;
	}

	public NodeConfiguration getNodeConfiguration() {
		return nodeConfiguration;
	}

}
