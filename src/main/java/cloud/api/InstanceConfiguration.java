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
package cloud.api;

import cloud.common.NodeConfiguration;
import instance.Node;
import instance.common.Block;
import se.sics.kompics.address.Address;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

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
