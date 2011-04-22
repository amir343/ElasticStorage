package instance.common;

import se.sics.kompics.Init;
import cloud.common.NodeConfiguration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 */

public class DiskInit extends Init {

	private NodeConfiguration nodeConfiguration;

	public DiskInit(NodeConfiguration nodeConfiguration) {
		this.nodeConfiguration = nodeConfiguration;
	}

	public NodeConfiguration getNodeConfiguration() {
		return nodeConfiguration;
	}

}
