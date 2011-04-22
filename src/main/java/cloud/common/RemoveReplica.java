package cloud.common;

import scenarios.manager.Cloud.Node;
import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-28
 *
 */

public class RemoveReplica extends Event {
	
	private Node node;

	public RemoveReplica(Node node) {
		this.node = node;
	}

	public Node getNode() {
		return node;
	}
}
