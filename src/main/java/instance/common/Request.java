package instance.common;

import java.io.Serializable;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class Request extends Event implements Serializable {
	
	private static final long serialVersionUID = 8315923390829741468L;
	private String id;
	private String blockId;
	private Address destinationNode;
	
	public Request(String id, String blockID) {
		this.id = id;
		this.blockId = blockID;
		this.destinationNode = null;
	}

	public Request(String id, String blockID, Address destinationNode) {
		this.id = id;
		this.blockId = blockID;
		this.destinationNode = destinationNode;
	}

	public String getId() {
		return id;
	}

	public String getBlockId() {
		return blockId;
	}
	
	public Address getDestinationNode() {
		return destinationNode;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
			sb.append("id: ").append(id).append(", ");
			sb.append("blockId: ").append(blockId);
		sb.append("}");
		return sb.toString();
	}

}
