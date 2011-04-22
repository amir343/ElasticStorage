package econtroller.sensor;

import instance.os.MonitorPacket;
import instance.os.MonitorResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;
import cloud.api.NewNodeToMonitor;
import econtroller.controller.Sense;
import econtroller.controller.StartSense;
import econtroller.gui.ControllerGUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class Sensor extends ComponentDefinition {

	private ControllerGUI gui = ControllerGUI.getInstance();
	private Logger logger = LoggerFactory.getLogger(Sensor.class, gui);
	
	Negative<SensorChannel> sensorChannel = provides(SensorChannel.class);
	Positive<Timer> timer = requires(Timer.class);
	Positive<Network> network = requires(Network.class);
	
	private Set<Address> currentNodes = new HashSet<Address>();
	private Map<Address, List<MonitorPacket>> monitors = new HashMap<Address, List<MonitorPacket>>();
	protected Address self;
	private UUID lastTimeout;
	private long SENSE_INTERVAL = 5000;
	
	public Sensor() {
		subscribe(initHandler, control);
		
		subscribe(senseHandler, sensorChannel);
		subscribe(startSenseHandler, sensorChannel); 
		subscribe(stopSenseHandler, sensorChannel);
		
		subscribe(senseTimeout, timer);
		
		subscribe(monitorResponseHandler, network);
		subscribe(newNodeToMonitorHandler, network);		
	}
	
	Handler<SensorInit> initHandler = new Handler<SensorInit>() {
		@Override
		public void handle(SensorInit event) {
			logger.info("Sensor component started");
			self = event.getControllerConfiguration().getSelfAddress();
		}
	};
	protected boolean enabled;
	
	Handler<StartSense> startSenseHandler = new Handler<StartSense>() {
		@Override
		public void handle(StartSense event) {
			enabled = true;
			SENSE_INTERVAL = event.getSenseTimeout()*1000;
			scheduleSensingNodes();
		}
	};
	
	Handler<StopSense> stopSenseHandler = new Handler<StopSense>() {
		@Override
		public void handle(StopSense event) {
			enabled = false;
			cancelAnyPreviousTimer();			
		}
	};
	
	Handler<Sense> senseHandler = new Handler<Sense>() {
		@Override
		public void handle(Sense event) {
			synchronized (currentNodes) {
				currentNodes.addAll(event.getNodes());
			}
		}
	};
	
	Handler<SenseTimeout> senseTimeout = new Handler<SenseTimeout>() {
		@Override
		public void handle(SenseTimeout event) {
			sendOutMonitorMessage();
			scheduleSensingNodes();
		}
	};
	
	Handler<MonitorResponse> monitorResponseHandler = new Handler<MonitorResponse>() {
		@Override
		public void handle(MonitorResponse event) {
			logger.info("Got back MonitorResponse from " + event.getSource());
			if (null == monitors.get(event.getSource())) {
				monitors.put(event.getSource(), new ArrayList<MonitorPacket>());
			}
			monitors.get(event.getSource()).add(event.getMonitorPacket());
			trigger(event, sensorChannel);
		}
	};
	
	Handler<NewNodeToMonitor> newNodeToMonitorHandler = new Handler<NewNodeToMonitor>() {
		@Override
		public void handle(NewNodeToMonitor event) {
			synchronized (currentNodes) {
				currentNodes.add(event.getNewNode());
			}
		}
	};

	protected void scheduleSensingNodes() {
		if (enabled) {
			ScheduleTimeout st = new ScheduleTimeout(SENSE_INTERVAL);
			SenseTimeout sense = new SenseTimeout(st);
			st.setTimeoutEvent(sense);
			lastTimeout = sense.getTimeoutId();
			trigger(st, timer);		
		}
	}

	protected void cancelAnyPreviousTimer() {
		CancelTimeout cancelTimeout = new CancelTimeout(lastTimeout);
		trigger(cancelTimeout, timer);
	}

	protected void sendOutMonitorMessage() {
		synchronized (currentNodes) {
			for (Address address : currentNodes) {
				trigger(new Monitor(self, address), network);
			}
		}
		
	}
}
