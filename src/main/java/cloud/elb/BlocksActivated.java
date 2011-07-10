package cloud.elb;

import instance.common.Block;
import se.sics.kompics.Event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-14
 *
 */

public class BlocksActivated extends Event {

	private Set<Block> blocks = new HashSet<Block>();
	
	public BlocksActivated() {
		
	}
	
	public BlocksActivated(List<Block> blocks) {
		this.blocks.addAll(blocks);
	}
	
	public void addBlock(Block block) {
		this.blocks.add(block);
	}

	public Set<Block> getBlocks() {
		return blocks;
	}

}
