package econtroller.design.impl;

import cloud.elb.SenseData;
import econtroller.controller.Controller;
import econtroller.design.ControllerDesign;
import econtroller.fuzzy.FuzzyInferenceEngine;
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
    private FuzzyInferenceEngine fuzzyEngine = new FuzzyInferenceEngine();
	
/*
    One of the best results so far
    private final double k1 = 0.161096566166813;
    private final double k2 = -2.34614217089670e-06;
    private final double k3 = 1.08954419535251e-06;
    private final double k4 = -2.01946724014184e-11;
*/

    private final double k1 = 0.36003;
    private final double k2 = -1.1485e-05;
    private final double k3 = 1.9555e-06;
    private final double k4 = -8.0729e-12;

	private double cpuLoadAverage;
	private double cpuSTD;
	private double totalCost;
	private double responseTimeAverage;
    private int numberOfNodes = 0;

    private double lastAverageCpuSTD;

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
        boolean scaleUp = controlInput > numberOfNodes;
        if ( fuzzyEngine.act(scaleUp, lastAverageCpuSTD) )
            controller.actuate(controlInput, numberOfNodes);
        else
            logger.info("Not acting this time");
	}
	
	private double getControlInput() {
        lastAverageCpuSTD = cpuSTD / nCpuSTD;
		double controlInput = k1*(cpuLoadAverage/ nCpuLoad -30) +
                              k2*(lastAverageCpuSTD) +
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
