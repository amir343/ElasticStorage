package econtroller.design.impl;

import instance.os.MonitorPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.address.Address;
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

	private static final double THRESHOLD = 1;
	private Logger logger = LoggerFactory.getLogger(NaiveController.class, ControllerGUI.getInstance());
	private ConcurrentHashMap<Address, List<MonitorPacket>> monitors = new ConcurrentHashMap<Address, List<MonitorPacket>>();
	private Controller controller;
	
	public NaiveController() {
		
	}

	@Override
	public void sense(Address source, MonitorPacket monitorPacket) {
		logger.debug("Adding monitor " + monitorPacket.toString());
		if (monitors.get(source) == null) {
			List<MonitorPacket> mon = new ArrayList<MonitorPacket>();
			monitors.put(source, mon);
		}
		monitors.get(source).add(monitorPacket);
	}

	@Override
	public void action() {
		if (computeAverage()) {
			logger.warn("Ordering a new node to cloudProvider");
			controller.actuate();
		} else {
			logger.warn("Everything is under control!");
		}
	}

	private boolean computeAverage() {
		synchronized (monitors) {
			for (Address address : monitors.keySet()) {
				double average = 0;
				int size = monitors.get(address).size();
				for (MonitorPacket mp : monitors.get(address)) {
					average += mp.getCpuLoad();
				}
				average /= size;
				logger.debug("Average = " + average);
				synchronized (monitors) {
					monitors.clear();
				}
				if (average > THRESHOLD) return true;
			}
			return false;
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
