package cloud.api;

import instance.InstanceProcess;
import instance.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import logger.Logger;
import scenarios.manager.CloudConfiguration;
import se.sics.kompics.address.Address;
import cloud.common.NodeConfiguration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class InstanceManagement {

	private CloudConfiguration cloudConfiguration;
	private ConcurrentMap<Node, InstanceProcess> currentProcesses = new ConcurrentHashMap<Node, InstanceProcess>();
	private Logger logger;

	public InstanceManagement(CloudConfiguration cloudConfiguration, Logger logger) {
		this.cloudConfiguration = cloudConfiguration;
		this.logger = logger;
	}

	public void initializeInstance(List<NodeConfiguration> nodes) {
		for (NodeConfiguration node : nodes) {
			initializeNode(node);
		}
	}

	public void initializeNode(NodeConfiguration node) {
		InstanceProcess instanceProcess = new InstanceProcess(cloudConfiguration, node);
		instanceProcess.start();
		currentProcesses.put(node.getNode(), instanceProcess);
	}
	
	public void kill(Node node) {
		currentProcesses.get(node).kill();
		currentProcesses.remove(node);
	}
	
	public List<Address> getAllNodes() {
		List<Address> nodes = new ArrayList<Address>();
		for (Node node : currentProcesses.keySet()) {
			nodes.add(node.getAddress());
		}
		return nodes;
	}

}
