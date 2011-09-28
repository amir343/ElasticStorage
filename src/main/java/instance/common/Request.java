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
package instance.common;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

import java.io.Serializable;

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
