package econtroller.controller;

import instance.os.MonitorResponse;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;
import econtroller.ControllerConfiguration;
import econtroller.design.ControlRepository;
import econtroller.design.ControllerDesign;
import econtroller.gui.ControllerGUI;
import econtroller.sensor.SensorChannel;
import econtroller.sensor.StopSense;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class Controller extends ComponentDefinition {

	private ControllerGUI gui = ControllerGUI.getInstance();
	private Logger logger = LoggerFactory.getLogger(Controller.class, gui);
	
	Positive<SensorChannel> sensor = requires(SensorChannel.class);
	Positive<Network> network = requires(Network.class);
	Positive<Timer> timer = requires(Timer.class);
	
	private ControlRepository controlRespository;
	protected Address self;
	protected ControllerConfiguration controllerConfiguration;
	protected boolean connectedToCloudProvider = false;
	private static final long CONNECTION_TIMEOUT = 5000;
	private static long ACTION_PERFORMING_INTERVAL = 60000;
	private Address cloudProviderAddress;
	private ControllerDesign controller = null;
	private UUID previousActionPerformingTimeout;
	private boolean actionPerformingEnabled = false;
	
	public Controller() {
		gui.setController(this);
		
		subscribe(initHandler, control);
		
		subscribe(connectionTimeoutHandler, timer);
		subscribe(actuateTimeoutHandler, timer);
		
		subscribe(monitorResponseHandler, sensor);
		
		subscribe(connectionEstablishedHandler, network);
	}

	Handler<ControllerInit> initHandler = new Handler<ControllerInit>() {
		@Override
		public void handle(ControllerInit event) {
			logger.info("Controller component is started...");
			controllerConfiguration = event.getControllerConfiguration();
			self = event.getControllerConfiguration().getSelfAddress();
			setGUITitle();
			controlRespository = ControlRepository.getInstance();
			gui.addControllers(controlRespository.getControllerNames());
		}
	};
	
	Handler<ConnectionEstablished> connectionEstablishedHandler = new Handler<ConnectionEstablished>() {
		@Override
		public void handle(ConnectionEstablished event) {
			logger.info("Connecterd to cloud provider.");
			connectedToCloudProvider = true;
			trigger(new Sense(event.getNodes()), sensor);
		}
	}; 
	
	Handler<ConnectionTimeout> connectionTimeoutHandler = new Handler<ConnectionTimeout>() {
		@Override
		public void handle(ConnectionTimeout event) {
			if (!connectedToCloudProvider) {
				gui.enableConnectionSection();
				logger.warn("Cloud provider seems to be down");
			}
		}
	};
	
	Handler<MonitorResponse> monitorResponseHandler = new Handler<MonitorResponse>() {
		@Override
		public void handle(MonitorResponse event) {
			if (controller != null) {
				controller.sense(event.getSource(), event.getMonitorPacket());
			}
		}
	};
	
	Handler<ActuateTimeout> actuateTimeoutHandler = new Handler<ActuateTimeout>() {
		@Override
		public void handle(ActuateTimeout event) {
			if (controller != null) {
				controller.action();
			}
			scheduleActionPerforming();
		}
	};

	protected void setGUITitle() {
		gui.setTitle("Controller@" + controllerConfiguration.getIp() + ":" + controllerConfiguration.getPort());		
	}

	protected void scheduleActionPerforming() {
		if (actionPerformingEnabled) {
			ScheduleTimeout st = new ScheduleTimeout(ACTION_PERFORMING_INTERVAL);
			ActuateTimeout action = new ActuateTimeout(st);
			st.setTimeoutEvent(action);
			previousActionPerformingTimeout = action.getTimeoutId();
			trigger(st, timer);		
		}
	}

	public void connectToCloudProvider(String ip, String port) {
		cloudProviderAddress = getAddressFor(ip, port);
		trigger(new Connect(self, cloudProviderAddress), network);
		scheduleConnectionTimeout();
	}

	private void scheduleConnectionTimeout() {
		ScheduleTimeout st = new ScheduleTimeout(CONNECTION_TIMEOUT);
		st.setTimeoutEvent(new ConnectionTimeout(st));
		trigger(st, timer);
	}

	public void disconnectFromCloudProvider() {
		if (cloudProviderAddress != null) { 
			trigger(new Disconnect(self, cloudProviderAddress), network);
			logger.info("Disconnected from Cloud Provider");
		}
		else
			logger.warn("No Cloud Provider is available to disconnect from.");
	}
	
	public Address getAddressFor(String ip, String port) {
		Address address = null;
		try {
			address = new Address(InetAddress.getByName(ip), Integer.parseInt(port), 1);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Host is not defined correctly: " + ip);
		}
		return address;
	}

	public void startController(String selectedController, int senseTimeout, int actTimeout) {
		trigger(new StartSense(senseTimeout), sensor);
		ACTION_PERFORMING_INTERVAL = actTimeout*1000;
		actionPerformingEnabled = true;
		scheduleActionPerforming();
		controller = controlRespository.getControllerWithName(selectedController);	
		controller.setControllerCallBack(this);
	}

	public void stopController() {
		actionPerformingEnabled = false;
		controller = null;
		cancelPreviousActionPerformingTimer();
		trigger(new StopSense(), sensor);
	}

	private void cancelPreviousActionPerformingTimer() {
		CancelTimeout cancelTimeout = new CancelTimeout(previousActionPerformingTimeout);
		trigger(cancelTimeout, timer);		
	}

	public void actuate() {
		if (connectedToCloudProvider)
			trigger(new NewNodeRequest(self, cloudProviderAddress), network);	
		else
			logger.warn("You should first connect to cloud provider");
	}
	
}
