package cloud.api;

import java.util.HashMap;
import java.util.Map;

import cloud.gui.CloudGUI;

import logger.Logger;
import logger.LoggerFactory;

import scenarios.manager.Cloud.Node;
import se.sics.kompics.address.Address;

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
