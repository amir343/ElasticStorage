package instance;

import org.apache.commons.lang.builder.HashCodeBuilder;
import se.sics.kompics.address.Address;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-21
 *
 */


public final class Node implements Serializable {

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
		return builder.toHashCode();
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
