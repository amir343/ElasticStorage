package instance.os;

import instance.common.Block;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-21
 *
 */

public class RebalanceResponse extends Message {

	private static final long serialVersionUID = -5047087718481425574L;
	private Block block;

	protected RebalanceResponse(Address source, Address destination, Block block) {
		super(source, destination);
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}

}
