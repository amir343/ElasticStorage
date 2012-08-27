///**
// * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package econtroller.sensor;
//
//import cloud.api.NewNodeToMonitor;
//import cloud.api.RequestSensingData;
//import cloud.elb.SenseData;
//import econtroller.controller.Sense;
//import econtroller.controller.StartSense;
//import econtroller.gui.ControllerGUI;
//import logger.Logger;
//import logger.LoggerFactory;
//import se.sics.kompics.ComponentDefinition;
//import se.sics.kompics.Handler;
//import se.sics.kompics.Negative;
//import se.sics.kompics.Positive;
//import se.sics.kompics.address.Address;
//import se.sics.kompics.network.Network;
//import se.sics.kompics.timer.CancelTimeout;
//import se.sics.kompics.timer.ScheduleTimeout;
//import se.sics.kompics.timer.Timer;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.UUID;
//
///**
// * 
// * @author Amir Moulavi
// * @date 2011-04-11
// *
// */
//
//public class Sensor extends ComponentDefinition {
//
//	private ControllerGUI gui = ControllerGUI.getInstance();
//	private Logger logger = LoggerFactory.getLogger(Sensor.class, gui);
//	
//	Negative<SensorChannel> sensorChannel = provides(SensorChannel.class);
//	Positive<Timer> timer = requires(Timer.class);
//	Positive<Network> network = requires(Network.class);
//	
//	private Set<Address> currentNodes = new HashSet<Address>();
//	protected Address self;
//	private Address cloudProvider;
//	private UUID lastTimeout;
//	private long SENSE_INTERVAL = 5000;
//	private boolean enabled;
//	
//	public Sensor() {
//		subscribe(initHandler, control);
//		
//		subscribe(senseHandler, sensorChannel);
//		subscribe(startSenseHandler, sensorChannel); 
//		subscribe(stopSenseHandler, sensorChannel);
//		
//		subscribe(senseTimeout, timer);
//		
//		subscribe(monitorResponseHandler, network);
//		subscribe(newNodeToMonitorHandler, network);		
//	}
//	
//	Handler<SensorInit> initHandler = new Handler<SensorInit>() {
//		@Override
//		public void handle(SensorInit event) {
//			logger.info("Sensor component started");
//			self = event.getControllerConfiguration().getSelfAddress();
//		}
//	};
//	
//	/**
//	 * This handler is triggered when controller issues a START signal with a frequency of sensing
//	 */
//	Handler<StartSense> startSenseHandler = new Handler<StartSense>() {
//		@Override
//		public void handle(StartSense event) {
//			enabled = true;
//			SENSE_INTERVAL = event.getSenseTimeout()*1000;
//			scheduleSensingNodes();
//		}
//	};
//	
//	/**
//	 * This handler is triggered when controller issues a STOP signal to the Sensor
//	 */
//	Handler<StopSense> stopSenseHandler = new Handler<StopSense>() {
//		@Override
//		public void handle(StopSense event) {
//			enabled = false;
//			cancelAnyPreviousTimer();			
//		}
//	};
//	
//	/**
//	 * This handler is triggered when controller sends a set of instances that Sensor should sense
//	 */
//	Handler<Sense> senseHandler = new Handler<Sense>() {
//		@Override
//		public void handle(Sense event) {
//			cloudProvider = event.getCloudProvider();
//		}
//	};
//	
//	/**
//	 * This handler is triggered to send out monitoring message and schedule the next sensing
//	 */
//	Handler<SenseTimeout> senseTimeout = new Handler<SenseTimeout>() {
//		@Override
//		public void handle(SenseTimeout event) {
//			sendOutMonitorMessage();
//			scheduleSensingNodes();
//		}
//	};
//	
//	/**
//	 * This handler is triggered when the sensor receives monitor response from an instance
//	 */
//	Handler<SenseData> monitorResponseHandler = new Handler<SenseData>() {
//		@Override
//		public void handle(SenseData event) {
//			logger.info(event.toString());
//			trigger(event, sensorChannel);
//		}
//	};
//	
//	/**
//	 * This handler is triggered when a new instance joins the cloud environment
//	 */
//	Handler<NewNodeToMonitor> newNodeToMonitorHandler = new Handler<NewNodeToMonitor>() {
//		@Override
//		public void handle(NewNodeToMonitor event) {
//			synchronized (currentNodes) {
//				currentNodes.add(event.getNewNode());
//			}
//		}
//	};
//
//	private void scheduleSensingNodes() {
//		if (enabled) {
//			ScheduleTimeout st = new ScheduleTimeout(SENSE_INTERVAL);
//			SenseTimeout sense = new SenseTimeout(st);
//			st.setTimeoutEvent(sense);
//			lastTimeout = sense.getTimeoutId();
//			trigger(st, timer);		
//		}
//	}
//
//	private void cancelAnyPreviousTimer() {
//		CancelTimeout cancelTimeout = new CancelTimeout(lastTimeout);
//		trigger(cancelTimeout, timer);
//	}
//
//	private void sendOutMonitorMessage() {
//		trigger(new RequestSensingData(self, cloudProvider), network);
//	}
//}
