package instance.common;

import cloud.api.InstanceConfiguration;
import cloud.common.NodeConfiguration;
import instance.Node;
import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 */

public class OSInit extends Init {

	private InstanceConfiguration instanceConfiguration;
	
	public OSInit(InstanceConfiguration instanceConfiguration) {
		this.instanceConfiguration = instanceConfiguration;
	}
	
	public List<Block> getBlocks() {
		return instanceConfiguration.getBlocks();
	}

	public Address getCloudProviderAddress() {
		return instanceConfiguration.getCloudProviderAddress();
	}
	
	public Address getSelfAddress() {
		return instanceConfiguration.getSelfAddress();
	}

	public Node getNodeInfo() {
		return instanceConfiguration.getNode();
	}
	
	public NodeConfiguration getNodeConfiguration() {
		return instanceConfiguration.getNodeConfiguration();
	}
}
