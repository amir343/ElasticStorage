package instance.common;

import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 20111-03-19
 *
 */

public class BlockResponse extends Event {
	
	private Block block;
	private instance.os.Process process;

	public BlockResponse(Block block) {
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}

	public void setProcess(instance.os.Process process2) {
		this.process = process2;		
	}
	
	public instance.os.Process getProcess() {
		return process;
	}
	
}
