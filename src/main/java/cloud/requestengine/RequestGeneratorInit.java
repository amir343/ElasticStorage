package cloud.requestengine;

import instance.common.Block;

import java.util.List;

import se.sics.kompics.Init;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-30
 *
 */

public class RequestGeneratorInit extends Init {

	private List<Block> blocks;

	public RequestGeneratorInit(List<Block> blocks) {
		this.blocks = blocks;
	}
	
	public List<Block> getBlocks() {
		return blocks;
	}

}
