package econtroller.design;

import econtroller.controller.Controller;
import instance.os.MonitorPacket;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-18
 *
 */


public interface ControllerDesign {

	void sense(Address address, MonitorPacket monitorPacket);
	
	void action();

	void setControllerCallBack(Controller controller);

	ControllerDesign clone();
}
