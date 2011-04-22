package cloud.api;

import instance.common.Block;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import scenarios.manager.Cloud.Node;
import se.sics.kompics.address.Address;
import cloud.common.NodeConfiguration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class InstanceConfiguration implements Serializable {

	private static final long serialVersionUID = -6620752752241044515L;
	private Address cloudProviderAddress;
	private Node node;
	private NodeConfiguration nodeConfiguration;

	public InstanceConfiguration(NodeConfiguration node, Address cloudProviderAddress) {
		this.nodeConfiguration = node;
		this.node = node.getNode();
		this.cloudProviderAddress = cloudProviderAddress;
	}

	public Address getCloudProviderAddress() {
		return cloudProviderAddress;
	}

	public Node getNode() {
		return node;
	}
	
	public NodeConfiguration getNodeConfiguration() {
		return nodeConfiguration;
	}
	
	public List<Block> getBlocks() {
		return nodeConfiguration.getBlocks();
	}

	public Address getSelfAddress() {
		Address self = null;
		try {
			self = new Address(InetAddress.getByName(node.getIP()), node.getPort(), 1);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return self;
	}
	
	public static InstanceConfiguration load(String topologyFile) {
		InstanceConfiguration topology = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(topologyFile));
			topology  = (InstanceConfiguration) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return topology;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
			sb.append("nodeConfig: ").append(nodeConfiguration).append(",");
			sb.append("cloud: ").append(cloudProviderAddress).append(",");
		sb.append("}");
		return sb.toString();
	}
	
}
