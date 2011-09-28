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
package cloud.elb;

import instance.Node;
import instance.common.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-28
 *
 */

public class ELBEntry {

	private String name;
	private long size;
	private boolean active = false;
	private List<Node> replicas = new ArrayList<Node>();
	private List<Node> suspected = new ArrayList<Node>();
	private LoadBalancerAlgorithm algorithm = LeastCPULoadAlgorithm.getInstance();
	
	public ELBEntry(String name, long size) {
		this.name = name;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	public boolean isActive() {
		return active;
	}

	public List<Node> getReplicas() {
		return replicas;
	}
	
	public int getNrOfReplicas() {
		return replicas.size();
	}
	
	public Block getBlock() {
		Block block = new Block(name, size);
		return block;
	}

	public void suspect(Node node) {
		if (replicas.contains(node)) {
			replicas.remove(node);
			suspected.add(node);
		}
		if (replicas.size() == 0) active = false;
	}
	
	public void restore(Node node) {
		if (suspected.contains(node)) {
			suspected.remove(node);
			replicas.add(node);
		}
		if (replicas.size() > 0) active = true;
	}

	public void removeFor(Node node) {
		suspected.remove(node);
		replicas.remove(node);
		if (replicas.size() == 0) active = false;
	}

	public void activateFor(Node node) {
		if (!replicas.contains(node)) {
			active = true;		
			replicas.add(node);
		} 
	}
	
	public Node getNextNodeToSendRequest() {
		Node node = algorithm.getNextNodeFrom(replicas);
		return node;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
			sb.append("block: ").append(getBlock());
		sb.append("}");
		return sb.toString();
	}

}
