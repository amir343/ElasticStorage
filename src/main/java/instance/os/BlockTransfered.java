package instance.os;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-21
 *
 */

public class BlockTransfered extends Message {

	private static final long serialVersionUID = 2315832526699716455L;
	private long blockSize;

	public BlockTransfered(Address source, Address destination, long blockSize) {
		super(source, destination);
		this.blockSize = blockSize;
	}

	public long getBlockSize() {
		return blockSize;
	}

}
