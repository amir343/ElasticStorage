package cloud.common;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class SendRawData extends Event {
	
	private Address controller;
	private int nrNodes;

	public SendRawData(Address controller, int numberOfNodes) {
		this.controller = controller;
		this.nrNodes = numberOfNodes;
	}

	public Address getController() {
		return controller;
	}

	public int getNrNodes() {
		return nrNodes;
	}

}
