package instance.common;

import se.sics.kompics.Event;

import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class LoadBlock extends Event {

	private List<Block> blocks;

	public LoadBlock(List<Block> blocks) {
		this.blocks = blocks;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

}
