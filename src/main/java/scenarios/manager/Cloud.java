package scenarios.manager;

import instance.Instance;
import instance.common.Block;
import instance.common.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

import se.sics.kompics.address.Address;
import cloud.CloudProvider;
import cloud.common.NodeConfiguration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-23
 *
 */

public abstract class Cloud {

	private Class<? extends CloudProvider> cloudClass;
	private List<NodeConfiguration> nodeConfigurations = new ArrayList<NodeConfiguration>();
	private CloudConfiguration cloudConfiguration = new CloudConfiguration();
	private final String classPath = System.getProperty("java.class.path");
	private final List<String> blockNames = new ArrayList<String>();
	private final List<Block> blocks = new ArrayList<Block>();
	private File cloudConfigurationFile;

	public Cloud(Class<? extends CloudProvider> cloudClass, Class<? extends Instance> instanceClass) {
		this.cloudClass = cloudClass;
		this.cloudConfiguration.setInstanceClass(instanceClass);
	}
	
	protected NodeConfiguration node(String nodeName, String address, int port) {
		Node node = new Node(nodeName, address, port);
		NodeConfiguration nodeConfiguration = new NodeConfiguration();
		nodeConfiguration.setNodeInfo(node);
		nodeConfigurations.add(nodeConfiguration);
		return nodeConfiguration;
	}
	
	protected void cloudProviderAddress(String address, int port) {
		cloudConfiguration.setCloudProviderAddress(address, port);
	}
	
	protected void data(String name, int size, Size sizeInBytes) {
		if (blockNames.contains(name)) {
			throw new RuntimeException("Data block with name '" + name + "' already exists -> data(\"" + name+ "\", " + size +", Size." + sizeInBytes +");");
		}
		blockNames.add(name);
		Block block = new Block(name, size*sizeInBytes.getSize());
		blocks.add(block);
	}
	
	protected void replicationDegree(int replicationDegree) {
		cloudConfiguration.setReplicationDegree(replicationDegree);
	}
	
	protected void addressPoll(String addressPollXmlFileName) {
		cloudConfiguration.setAddressPollXmlFilename(addressPollXmlFileName);
	}

	public void start() {
		cloudConfiguration.addNodeConfiguration(nodeConfigurations);
		cloudConfiguration.addBlockData(blocks);
		cloudConfiguration.validate();
		writeCloudTopologyObjectIntoTempFile();
		startCloudProviderProcess();
	}
	
	private void startCloudProviderProcess() {
		CloudProcess cp = new CloudProcess(classPath, cloudConfigurationFile.getAbsolutePath(), cloudClass.getCanonicalName());
		cp.start();
	}

	private void writeCloudTopologyObjectIntoTempFile() {
		File file = null;
		try {
			file = File.createTempFile("cloudConfiguration", ".bin");
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(cloudConfiguration);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		cloudConfigurationFile = file.getAbsoluteFile();
	}

	public static final class Node implements Serializable {

		private static final long serialVersionUID = -5493507935643715175L;
		private String nodeName;
		private String address;
		private int port;

		public Node(String nodeName, String address, int port) {
			this.nodeName = nodeName;
			this.address = address;
			this.port = port;
		}

		public String getNodeName() {
			return nodeName;
		}

		public String getIP() {
			return address;
		}

		public int getPort() {
			return port;
		}
		
		public String getAddressStringWithoutName() {
			return address + ":" + port;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(nodeName).append("@").append(address).append(":").append(port);
			return sb.toString();
		}
		
		public Address getAddress() {
			Address self = null;
			try {
				self = new Address(InetAddress.getByName(address), port, 1);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return self;
		}

		public static Node fromString(String nodeString) {
			String[] tokens = nodeString.split("@");
			String[] add = tokens[1].split(":");
			return new Node(tokens[0], add[0], Integer.parseInt(add[1]));
		}
		
		public boolean equals(Object another) {
			if (another == null) return false;
			if (!(another instanceof Node)) return false;
			Node an = (Node) another;
			if (!an.getIP().equals(this.getIP())) return false;
			if (!an.getNodeName().equals(this.getNodeName())) return false;
			if (an.getPort() != this.getPort()) return false;
			return true;
		}
		
		public int hashCode() {
			HashCodeBuilder builder = new HashCodeBuilder();
			builder.append(this.port);
			builder.append(this.address);
			builder.append(this.nodeName);
			return builder.hashCode();
		}

		public static Address getAddressFromString(String string) {
			Address self = null;
			String[] tokens = string.split(":");
			try {
				self = new Address(InetAddress.getByName(tokens[0]), Integer.parseInt(tokens[1]), 1);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return self;			
		}
		
	}

}
