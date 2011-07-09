package econtroller.design.impl;

import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.address.Address;
import cloud.elb.SenseData;
import econtroller.controller.Controller;
import econtroller.design.ControllerDesign;
import econtroller.gui.ControllerGUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-18
 *
 */

public class NaiveController implements ControllerDesign {

	private static final double THRESHOLD = 30.0;
	private Logger logger = LoggerFactory.getLogger(NaiveController.class, ControllerGUI.getInstance());
	private Controller controller;
	private SenseData senseData;
	
	public NaiveController() {
		
	}

	@Override
	public void sense(Address source, SenseData monitorPacket) {
		logger.debug("Adding monitor " + monitorPacket.toString());
		this.senseData = monitorPacket;
	}

	@Override
	public void action() {
		if (senseData.getCpuLoad() > THRESHOLD) {
			logger.warn("Ordering a new node to cloudProvider");
			controller.actuate();
		} else {
			logger.warn("Everything is under control!");
		}
	}

	@Override
	public void setControllerCallBack(Controller controller) {
		this.controller = controller;
		logger.info("Controller [NaiveController] started.");
	}
	
	@Override
	public ControllerDesign clone() {
		return new NaiveController();
	}

	
}
