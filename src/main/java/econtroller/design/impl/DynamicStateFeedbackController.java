/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
	
    private final double k1 = 0.13436;
    private final double k2 = 1.4702e-06;
    private final double k3 = 0.0031878;

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
		this.totalCost += monitorPacket.getPeriodicTotalCost();
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
		double controlInput = k1*(cpuLoadAverage/ nCpuLoad -10) +
                              k2*(totalCost/nTotalCost) +
                              k3*(responseTimeAverage/nResponseTime);
		logger.info("Controller output: " + controlInput);
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
