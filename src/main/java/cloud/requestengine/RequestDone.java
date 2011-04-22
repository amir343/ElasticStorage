package cloud.requestengine;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public class RequestDone extends Message {

	private static final long serialVersionUID = 4603408788313957328L;
	private String requestID;

	public RequestDone(Address source, Address destination, String requestID) {
		super(source, destination);
		this.requestID = requestID;
	}

	public String getRequestID() {
		return requestID;
	}

}
