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
    w/o std
	private final double k1 = 0.160360857683857;
	private final double k2 = -3.09586414167910e-06;
	private final double k3 = 2.29971358515257e-06;
	private final double k4 = -1.13957731029648e-12;
*/

/*
    w/o std
    private final double k1 = 0.153373490048025;
    private final double k2 = -1.22831042646208e-06;
    private final double k3 = -1.42252281334366e-06;
    private final double k4 = 3.71560770454631e-12;
*/

/*
    w/o std
    private final double k1 = 0.986607697307996;
    private final double k2 = 6.22158940678969e-07;
    private final double k3 = -2.00512860657761e-06;
    private final double k4 = -6.78988537284038e-13;
*/

    private final double k1 = 0.161096566166813;
    private final double k2 = -2.34614217089670e-06;
    private final double k3 = 1.08954419535251e-06;
    private final double k4 = -2.01946724014184e-11;

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
