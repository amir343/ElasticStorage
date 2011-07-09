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
 * @date 2011-07-09
 *
 */

public class DynamicStateFeedbackController implements ControllerDesign {

	private Logger logger = LoggerFactory.getLogger(DynamicStateFeedbackController.class, ControllerGUI.getInstance());
	private Controller controller;
	
	private final double k1 = 1.889978;
	private final double k2 = 0.000063684;
	private final double k3 = 0.0001345;
	private final double k4 = 0.0000000018818;
	private double cpuLoadAverage;
	private double bandwidthAverage;
	private double throughPutAverage;
	private double responseTimeAverage;
	
	
	@Override
	public void sense(Address address, SenseData monitorPacket) {
		this.cpuLoadAverage = monitorPacket.getCpuLoad();
		this.bandwidthAverage = monitorPacket.getBandwidthMean();
		this.throughPutAverage = monitorPacket.getAverageThroughput();
		this.responseTimeAverage = monitorPacket.getAverageResponseTime();
	}

	@Override
	public void action() {
		getControlInput();		
	}
	
	private double getControlInput() {
		double controlInput = k1*cpuLoadAverage + k3*throughPutAverage + k4*responseTimeAverage;
		logger.info("Control input: " + controlInput);
		return controlInput;
	}

	@Override
	public void setControllerCallBack(Controller controller) {
		this.controller = controller;
		
	}
	
	@Override
	public ControllerDesign clone() {
		return new DynamicStateFeedbackController();
	}

}
