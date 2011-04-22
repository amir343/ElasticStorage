package instance.common;

import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class WriteBlockIntoMemory extends Event {

	private Block block;

	public WriteBlockIntoMemory(Block block) {
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}
	
}
