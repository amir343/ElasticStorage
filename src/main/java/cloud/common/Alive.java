package cloud.common;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-27
 *
 */

public class Alive extends Message {

	private static final long serialVersionUID = -2424516031049654621L;

	public Alive(Address source, Address destination) {
		super(source, destination);
	}


}
