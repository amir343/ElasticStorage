package econtroller.controller;

import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-22
 *
 */

public class StartSense extends Event {

	private int senseTimeout;

	public StartSense(int senseTimeout) {
		this.senseTimeout = senseTimeout;
	}

	public int getSenseTimeout() {
		return senseTimeout;
	}

}
