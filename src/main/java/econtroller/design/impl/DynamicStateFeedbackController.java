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
	
/*
    One of the best results so far
    private final double k1 = 0.161096566166813;
    private final double k2 = -2.34614217089670e-06;
    private final double k3 = 1.08954419535251e-06;
    private final double k4 = -2.01946724014184e-11;
*/
/*
    sin load
    private final double k1 = -0.677918577035241;
    private final double k2 = 2.16009505663342;
    private final double k3 = -2.23943711236517;
    private final double k4 = -0.00387779118339479;
*/

/*
    private final double k1 = -0.636507125701686;
    private final double k2 = 0.110600631506731;
    private final double k3 = 0.0891767846540721;
    private final double k4 = 0.000391448763414515;
*/

    private final double k1 = 0.052542;
    private final double k2 = 0.23338;
    private final double k3 = -0.0097212;
    private final double k4 = 0.0025146;

	private double cpuLoadAverage;
	private double cpuSTD;
	private double totalCost;
	private double responseTimeAverage;
    private int numberOfNodes = 0;

    private int nCpuLoad = 0;
    private int nCpuSTD = 0;
    private int nTotalCost = 0;
    private int nResponseTime = 0;

	@Override
	public void sense(Address address, SenseData monitorPacket) {
		this.cpuLoadAverage += monitorPacket.getCpuLoadMean();
        nCpuLoad++;
		this.cpuSTD += monitorPacket.getCpuLoadSTD();
        nCpuSTD++;
		this.totalCost += monitorPacket.getTotalCost();
        nTotalCost++;
		this.responseTimeAverage += monitorPacket.getResponseTimeMean();
        nResponseTime++;
        this.numberOfNodes = monitorPacket.getNrNodes();
	}

	@Override
	public void action() {
		double controlInput = getControlInput();
        controller.actuate(controlInput, numberOfNodes);
	}
	
	private double getControlInput() {
		double controlInput = k1*(cpuLoadAverage/ nCpuLoad -30) +
                              k2*(cpuSTD / nCpuSTD) +
                              k3*(totalCost/nTotalCost) +
                              k4*(responseTimeAverage/nResponseTime);
		logger.info("Control input: " + controlInput);
        nCpuLoad = 0; nCpuSTD = 0; nTotalCost = 0; nResponseTime = 0;
        cpuLoadAverage = 0; cpuSTD = 0; totalCost = 0; responseTimeAverage = 0;
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
