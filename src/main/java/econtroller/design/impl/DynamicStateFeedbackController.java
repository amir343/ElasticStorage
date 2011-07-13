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
	private final double k1 = 0.160360857683857;
	private final double k2 = -3.09586414167910e-06;
	private final double k3 = 2.29971358515257e-06;
	private final double k4 = -1.13957731029648e-12;
*/

/*
    private final double k1 = 0.153373490048025;
    private final double k2 = -1.22831042646208e-06;
    private final double k3 = -1.42252281334366e-06;
    private final double k4 = 3.71560770454631e-12;
*/
    private final double k1 = 0.454648661683801;
    private final double k2 = 9.99512150205386e-06;
    private final double k3 = 1.51109935005030e-05;
    private final double k4 = -5.07701932112059e-11;

	private double cpuLoadAverage;
	private double bandwidthAverage;
	private double totalCost;
	private double responseTimeAverage;
    private int numberOfNodes = 0;

    private int nCpu = 0;
    private int nBandwidth = 0;
    private int nTotalCost = 0;
    private int nResponseTime = 0;

	@Override
	public void sense(Address address, SenseData monitorPacket) {
		this.cpuLoadAverage += monitorPacket.getCpuLoad();
        nCpu++;
		this.bandwidthAverage += monitorPacket.getBandwidthMean();
        nBandwidth++;
		this.totalCost += monitorPacket.getTotalCost();
        nTotalCost++;
		this.responseTimeAverage += monitorPacket.getAverageResponseTime();
        nResponseTime++;
        this.numberOfNodes = monitorPacket.numberOfNodes();
	}

	@Override
	public void action() {
		double controlInput = getControlInput();
        controller.actuate(controlInput, numberOfNodes);
	}
	
	private double getControlInput() {
		double controlInput = k1*(cpuLoadAverage/nCpu-10) +
                              k2*(bandwidthAverage/nBandwidth) +
                              k3*(totalCost/nTotalCost) +
                              k4*(responseTimeAverage/nResponseTime);
		logger.info("Control input: " + controlInput);
        nCpu = 0; nBandwidth = 0; nTotalCost = 0; nResponseTime = 0;
        cpuLoadAverage = 0; bandwidthAverage = 0; totalCost = 0; responseTimeAverage = 0;
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
