package cloud.api;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class NewNodeToMonitor extends Message {

	private static final long serialVersionUID = 1181948127603903930L;
	private Address newNode;

	public NewNodeToMonitor(Address source, Address destination, Address address) {
		super(source, destination);
		this.newNode = address;
	}

	public Address getNewNode() {
		return newNode;
	}

}
