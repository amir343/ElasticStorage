package econtroller.controller;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-20
 *
 */

public class NewNodeRequest extends Message {

	private static final long serialVersionUID = -7505388733307482046L;

	public NewNodeRequest(Address source, Address destination) {
		super(source, destination);
	}

}
