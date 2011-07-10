package scenarios.manager;

import cloud.common.NodeConfiguration;
import instance.Instance;
import instance.Node;
import instance.common.Block;
import org.apache.commons.lang.StringUtils;
import se.sics.kompics.address.Address;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-23
 *
 */

public class CloudConfiguration implements Serializable {
	
	private static final long serialVersionUID = 1763385801800113749L;
	private List<NodeConfiguration> nodeConfigurations = new ArrayList<NodeConfiguration>();
	private List<Node> nodes = new ArrayList<Node>();
	private String cloudProviderAddress;
	private int cloudProviderPort;
	private Class<? extends Instance> instanceClass;
	private List<Block> blocks;
	private int replicationDegree = 1;
	private String addressPollXmlFilename;

	public void addNodeConfiguration(NodeConfiguration nodeConfiguration) {
		this.nodeConfigurations.add(nodeConfiguration);
		this.nodes.add(nodeConfiguration.getNode());
	}

	public void addNodeConfiguration(List<NodeConfiguration> nodeConfigurations) {
		for (NodeConfiguration nc : nodeConfigurations) {
			addNodeConfiguration(nc);
		}		
	}

	public void setCloudProviderAddress(String cloudProviderAddress, int cloudProviderPort) {
		this.cloudProviderAddress = cloudProviderAddress;
		this.cloudProviderPort = cloudProviderPort;
	}

	public List<NodeConfiguration> getNodeConfigurations() {
		return nodeConfigurations;
	}
	
	public String getCloudProviderAddress() {
		return cloudProviderAddress;
	}

	public int getCloudProviderPort() {
		return cloudProviderPort;
	}

	public void setInstanceClass(Class<? extends Instance> instanceClass) {
		this.instanceClass = instanceClass;		
	}

	public Class<? extends Instance> getInstanceClass() {
		return instanceClass;
	}

	public void validate() {
		if (StringUtils.isBlank(getCloudProviderAddress())) {
			throw new RuntimeException("You have not specified any address for the cloud provider itself. Please do so by using address(Ip, port) function");
		}
		if (replicationDegree <= 0) {
			throw new RuntimeException("ReplicationDegree can not be less than or equal to 0");
		}
		if (addressPollXmlFilename == null) {
			throw new RuntimeException("You have to specify the addressPoll xml file name");
		}
	}
	
	public static CloudConfiguration load(String topologyFile) {
		CloudConfiguration topology = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(topologyFile));
			topology  = (CloudConfiguration) ois.readObject();
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

	public Address getSelfAddress() {
		Address self = null;
		try {
			self = new Address(InetAddress.getByName(cloudProviderAddress), cloudProviderPort, 1);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Host is not defined correctly: " + cloudProviderAddress);
		}
		return self;
	}

	public void addBlockData(List<Block> blocks) {
		this.blocks = blocks;		
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setReplicationDegree(int replicationDegree) {
		this.replicationDegree = replicationDegree;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public void setAddressPollXmlFilename(String addressPollXmlFileName) {
		this.addressPollXmlFilename = addressPollXmlFileName;
	}

	public String getAddressPollXmlFilename() {
		return addressPollXmlFilename;
	}
	
}
