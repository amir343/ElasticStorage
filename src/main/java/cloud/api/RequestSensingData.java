package cloud.api;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */

public class RequestSensingData extends Message {

	private static final long serialVersionUID = 8012003411245440962L;

	public RequestSensingData(Address source, Address destination) {
		super(source, destination);
	}

}
