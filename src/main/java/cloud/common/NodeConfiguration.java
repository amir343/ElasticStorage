package cloud.common;

import instance.Node;
import instance.common.Block;
import instance.common.Size;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-27
 *
 */

public class NodeConfiguration implements Serializable {
	
	private static final long serialVersionUID = -9025878066809528650L;
	private double cpuSpeed;
	private double bandwidth;
	private int simultaneousDownloads;
	private String name = "NONAME";
	private Node node;
	private List<Block> blocks;
	private int memory;
	private Node sourceNode;
	private Map<String, Address> dataBlocksMap;

	public NodeConfiguration(double cpuSpeed, double bandwidth, int memory, int simultaneousDownloads) {
		this.cpuSpeed = cpuSpeed;
		this.bandwidth = bandwidth;
		this.memory = memory;
		this.simultaneousDownloads = simultaneousDownloads;
	}

	public NodeConfiguration() {
		this.cpuSpeed = 2.0;
		this.bandwidth = 2.0;
		this.memory = 4;
		this.simultaneousDownloads = 20;
	}

	public long getCpuSpeedInstructionPerSecond() {
		long result = (long) ((cpuSpeed*10) * Size.GHertz.getSize());
		result /= 10;
		return result;
	}
	
	public double getCpuSpeed() {
		return cpuSpeed;
	}

	public long getBandwidthMegaBytePerSecond() {
		long result = (long) ((bandwidth*10) * Size.MB.getSize());
		result /= 10;
		return result;
	}
	
	public double getBandwidth() {
		return bandwidth;
	}

	public int getMemory() {
		return memory;
	}

	public int getSimultaneousDownloads() {
		return simultaneousDownloads;
	}
	
	public void setNodeName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setNodeInfo(Node node) {
		this.node = node;
		this.name = node.getNodeName();
	}

	public Node getNode() {
		return node;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
			sb.append("cpuSpeed: ").append(cpuSpeed).append(",");
			sb.append("memory: ").append(memory).append(",");
			sb.append("bandwidth: ").append(bandwidth).append(",");
			sb.append("simDownloads: ").append(simultaneousDownloads);
		sb.append("}");
		return sb.toString();
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;		
	}

	public List<Block> getBlocks() {
		return blocks;
	}
	
	public NodeConfiguration cpu(double speed) {
		this.cpuSpeed = speed;
		return this;
	}
	
	public NodeConfiguration memoryGB(int size) {
		this.memory = size;
		return this;
	}
	
	public NodeConfiguration bandwidthMB(double bandwidth) {
		this.bandwidth = bandwidth;
		return this;
	}

	public void setDataBlocksMap(Map<String, Address> dataBlocksMap) {
		this.dataBlocksMap = dataBlocksMap;
	}

	public Map<String, Address> getDataBlocksMap() {
		return dataBlocksMap;
	}
	
}
