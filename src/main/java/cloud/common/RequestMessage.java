package cloud.common;

import instance.common.Request;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-30
 *
 */

public class RequestMessage extends Message {

	private static final long serialVersionUID = 8682034389557178592L;
	private Request request;

	public RequestMessage(Address source, Address destination, Request request) {
		super(source, destination);
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}

}
