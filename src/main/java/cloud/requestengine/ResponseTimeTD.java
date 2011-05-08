package cloud.requestengine;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class ResponseTimeTD extends Event {

	private int nrNodes;
	private double mean;
	private Address controller;

	public ResponseTimeTD(int nrNodes, double mean, Address controller) {
		this.nrNodes = nrNodes;
		this.mean = mean;
		this.controller = controller;
	}

	public int getNrNodes() {
		return nrNodes;
	}

	public double getMean() {
		return mean;
	}

	public Address getController() {
		return controller;
	}

}
