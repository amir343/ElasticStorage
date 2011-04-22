package instance.common;

import java.io.Serializable;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

public class ShutDown extends Message implements Serializable {

	private static final long serialVersionUID = -5909108939372221250L;

	public ShutDown(Address source, Address destination) {
		super(source, destination);
	}


}
