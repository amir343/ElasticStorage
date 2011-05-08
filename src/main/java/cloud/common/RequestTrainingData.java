package cloud.common;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class RequestTrainingData extends Message {

	private static final long serialVersionUID = 4472163229708537671L;

	public RequestTrainingData(Address source, Address destination) {
		super(source, destination);
		// TODO Auto-generated constructor stub
	}

}
