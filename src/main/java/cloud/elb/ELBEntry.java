package cloud.elb;

import instance.Node;
import instance.common.Block;

import java.util.ArrayList;
import java.util.List;

import scenarios.manager.Cloud;

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

	public void updateNode(Node node) {
		replicas.add(node);	
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
		if (replicas.contains(node)) 
			active = true;		
		else replicas.add(node);
	}
	
	public Node getNextNodeToSendRequest() {
		Node node = algorithm.getNextNodeFrom(replicas);
		return node;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
			sb.append("block: ").append(getBlock());
		sb.append("{");
		return sb.toString();
	}
	
}
