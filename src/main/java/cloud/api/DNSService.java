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

import cloud.gui.CloudGUI;
import instance.Node;
import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.address.Address;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-28
 * 
 * <code>DNSService</code> represents Name/Address convertions
 */

public class DNSService {

	private Logger logger = LoggerFactory.getLogger(DNSService.class, CloudGUI.getInstance());
	
	private Map<Address, Node> addressSpace = new HashMap<Address, Node>();
	private Map<Node, Address> nodeSpace = new HashMap<Node, Address>();
	
	public DNSService() {
		
	}
	
	public void addDNSEntry(Node node) {
		Address address = node.getAddress();
		addressSpace.put(address, node);
		nodeSpace.put(node, address);
	}
	
	public Address getAddressForNode(Node node) {
		Address address = nodeSpace.get(node);
		if (address == null) {
			logger.error("Coud not find any DNS entry for node " + node);
		}
		return address;
	}
	
	public Node getNodeForAddress(Address address) {
		Node node = addressSpace.get(address);
		if (node == null) {
			logger.error("Coud not find any DNS entry for address " + address);
		}
		return node;
	}
	
}
