package econtroller.sensor;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class Monitor extends Message {

	private static final long serialVersionUID = -4732276884700905212L;

	public Monitor(Address source, Address destination) {
		super(source, destination);
	}

}
