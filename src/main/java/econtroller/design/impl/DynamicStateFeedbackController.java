package econtroller.design.impl;

import cloud.elb.SenseData;
import econtroller.controller.Controller;
import econtroller.design.ControllerDesign;
import econtroller.gui.ControllerGUI;
import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.address.Address;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */

public class DynamicStateFeedbackController implements ControllerDesign {

	private Logger logger = LoggerFactory.getLogger(DynamicStateFeedbackController.class, ControllerGUI.getInstance());
	private Controller controller;
	
//	private final double k1 = 1.10354401957889;
//	private final double k2 = 3.58775446101134e-05;
//	private final double k3 = 2.61080347041644e-05;
//	private final double k4 = 2.02728144216861e-10;
	private final double k1 = 0.160360857683857;
	private final double k2 = -3.09586414167910e-06;
	private final double k3 = 2.29971358515257e-06;
	private final double k4 = -1.13957731029648e-12;

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
