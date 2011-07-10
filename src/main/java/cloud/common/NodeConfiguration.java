package cloud.common;

import instance.Node;
import instance.common.Block;
import se.sics.kompics.address.Address;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-27
 *
 */

public class NodeConfiguration implements Serializable {
	
	private static final long serialVersionUID = -9025878066809528650L;
	private CpuConfiguration cpuConfiguration;
	private BandwidthConfiguration bandwidthConfiguration;
	private int simultaneousDownloads;
	private String name = "NONAME";
	private Node node;
	private List<Block> blocks;
	private int memory;
	private Map<String, Address> dataBlocksMap;

	public NodeConfiguration(double cpuSpeed, double bandwidth, int memory, int simultaneousDownloads) {
		this.cpuConfiguration = new CpuConfiguration(cpuSpeed);
		this.bandwidthConfiguration = new BandwidthConfiguration(bandwidth);
		this.memory = memory;
		this.simultaneousDownloads = simultaneousDownloads;
	}

	public NodeConfiguration() {
		this.cpuConfiguration = new CpuConfiguration(2.0);
		this.bandwidthConfiguration = new BandwidthConfiguration(2.0);
		this.memory = 4;
		this.simultaneousDownloads = 20;
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
			sb.append(cpuConfiguration).append(",");
			sb.append("memory: ").append(memory).append(",");
			sb.append(bandwidthConfiguration).append(",");
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
		this.cpuConfiguration = new CpuConfiguration(speed);
		return this;
	}
	
	public NodeConfiguration memoryGB(int size) {
		this.memory = size;
		return this;
	}
	
	public NodeConfiguration bandwidthMB(double bandwidth) {
		this.bandwidthConfiguration = new BandwidthConfiguration(bandwidth);
		return this;
	}

	public void setDataBlocksMap(Map<String, Address> dataBlocksMap) {
		this.dataBlocksMap = dataBlocksMap;
	}

	public Map<String, Address> getDataBlocksMap() {
		return dataBlocksMap;
	}

	public CpuConfiguration getCpuConfiguration() {
		return cpuConfiguration;
	}
	
	public BandwidthConfiguration getBandwidthConfiguration() {
		return bandwidthConfiguration;
	}
	
}
