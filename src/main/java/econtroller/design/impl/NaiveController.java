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
import econtroller.gui.ControllerGUI;
import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.address.Address;

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
    private int numberOfNodes;

    public NaiveController() {
		
	}

	@Override
	public void sense(Address source, SenseData monitorPacket) {
		logger.debug("Adding monitor " + monitorPacket.toString());
		this.senseData = monitorPacket;
        this.numberOfNodes = monitorPacket.getNrNodes();
	}

	@Override
	public void action() {
		if (senseData.getCpuLoadMean() > THRESHOLD) {
			logger.warn("Ordering a new node to cloudProvider");
			controller.actuate(1.0, numberOfNodes);
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
