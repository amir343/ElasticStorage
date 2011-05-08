package econtroller.modeler;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class ModelerInit extends Init {

	private Address self;

	public ModelerInit(Address self) {
		this.self = self;
	}
	
	public Address getSelf() {
		return self;
	}

}
