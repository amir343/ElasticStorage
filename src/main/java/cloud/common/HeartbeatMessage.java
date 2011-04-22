package cloud.common;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-27
 *
 */

public class HeartbeatMessage extends Message {

	private static final long serialVersionUID = -4970119975226325111L;

	public HeartbeatMessage(Address source, Address destination) {
		super(source, destination);
	}

}
