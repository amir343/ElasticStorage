package instance.os;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-21
 *
 */

public class RebalanceRequest extends Message {

	private static final long serialVersionUID = 2041241921246870055L;
	private String blockId;

	public RebalanceRequest(Address source, Address destination, String blockId) {
		super(source, destination);
		this.blockId = blockId;
	}

	public String getBlockId() {
		return blockId;
	}

}
