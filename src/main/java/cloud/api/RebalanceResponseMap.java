package cloud.api;

import se.sics.kompics.Event;
import cloud.common.NodeConfiguration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-21
 *
 */

public class RebalanceResponseMap extends Event {

	private NodeConfiguration nodeConfiguration;

	public RebalanceResponseMap(NodeConfiguration nodeConfiguration) {
		this.nodeConfiguration = nodeConfiguration;
	}
	
	public NodeConfiguration getNodeConfiguration() {
		return nodeConfiguration;
	}

}
