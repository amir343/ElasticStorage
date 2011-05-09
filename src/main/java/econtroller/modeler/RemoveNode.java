package econtroller.modeler;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-09
 *
 */

public class RemoveNode extends Message {

	private static final long serialVersionUID = 13388251491264537L;

	public RemoveNode(Address source, Address destination) {
		super(source, destination);
	}

}
