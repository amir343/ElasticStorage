package cloud.common;

import instance.common.Block;

import java.util.List;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-28
 *
 */

public class ELBInit extends Init {

	private List<Block> blocks;
	private int replicationDegree;
	private Address self;

	public ELBInit(List<Block> blocks, int replicationDegree, Address self) {
		this.blocks = blocks;
		this.replicationDegree = replicationDegree;
		this.self = self;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public Address getSelf() {
		return self;
	}

}
