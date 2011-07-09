package econtroller.design;

import se.sics.kompics.address.Address;
import cloud.elb.SenseData;
import econtroller.controller.Controller;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-18
 *
 */


public interface ControllerDesign {

	void sense(Address address, SenseData monitorPacket);
	
	void action();

	void setControllerCallBack(Controller controller);

	ControllerDesign clone();
}
