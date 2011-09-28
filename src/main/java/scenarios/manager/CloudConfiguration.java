/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package scenarios.manager;

import cloud.common.NodeConfiguration;
import instance.Instance;
import instance.Node;
import instance.common.Block;
import org.apache.commons.lang.StringUtils;
import se.sics.kompics.address.Address;

import javax.swing.plaf.synth.SynthLookAndFeel;
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
    private boolean headLess;

    private SLA sla;

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

    public boolean isHeadLess() {
        return headLess;
    }

    public void setHeadLess(boolean headLess) {
        this.headLess = headLess;
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

    public SLA getSla() {
        return sla;
    }

    public void setSla(SLA sla) {
        this.sla = sla;
    }

}
