package cloud.api;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-15
 *
 */

public class RestartInstance extends Message {

	private static final long serialVersionUID = -7230355120909401659L;

	public RestartInstance(Address source, Address destination) {
		super(source, destination);
	}

}
