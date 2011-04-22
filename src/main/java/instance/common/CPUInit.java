package instance.common;

import cloud.common.NodeConfiguration;
import se.sics.kompics.Init;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 */

public class CPUInit extends Init {

	private NodeConfiguration nodeConfiguration;

	public CPUInit(NodeConfiguration nodeConfiguration) {
		this.nodeConfiguration = nodeConfiguration;
	}

	public NodeConfiguration getNodeConfiguration() {
		return nodeConfiguration;
	}

}
