package econtroller.controller;

import cloud.elb.SenseData;
import econtroller.ControllerConfiguration;
import econtroller.actuator.ActuatorChannel;
import econtroller.actuator.NodeRequest;
import econtroller.design.ControlRepository;
import econtroller.design.ControllerDesign;
import econtroller.gui.ControllerGUI;
import econtroller.modeler.ModelPort;
import econtroller.modeler.StartModeler;
import econtroller.sensor.SensorChannel;
import econtroller.sensor.StopSense;
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

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
	Positive<ActuatorChannel> actuatorChannel = requires(ActuatorChannel.class);
	Positive<ModelPort> modeler = requires(ModelPort.class);
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
	
	/**
	 * This handler is triggered when the cloudProvider responds back to the connection establishment request
	 */
	Handler<ConnectionEstablished> connectionEstablishedHandler = new Handler<ConnectionEstablished>() {
		@Override
		public void handle(ConnectionEstablished event) {
			logger.info("Connecterd to cloud provider.");
			connectedToCloudProvider = true;
            gui.connectedToCloudProvider();
			trigger(new StartModeler(cloudProviderAddress), modeler);
			trigger(new Sense(cloudProviderAddress), sensor);
		}
	}; 
	
	/**
	 * This handler is triggered to check if the controller is connected to cloud provider or not
	 */
	Handler<ConnectionTimeout> connectionTimeoutHandler = new Handler<ConnectionTimeout>() {
		@Override
		public void handle(ConnectionTimeout event) {
			if (!connectedToCloudProvider) {
				gui.enableConnectionSection();
				gui.disableSystemIdentificationPanel();
                gui.notConnectedToCloudProvider();
				logger.warn("Cloud provider seems to be down");
			}
		}
	};
	
	/***
	 * This handler is triggered when it receives monitor information from sensor component	
	 */
	Handler<SenseData> monitorResponseHandler = new Handler<SenseData>() {
		@Override
		public void handle(SenseData event) {
			if (controller != null) {
				controller.sense(event.getSource(), event);
                trigger(event, modeler);
			}
		}
	};
	
	/**
	 * This handler is triggered periodically to initiate the controller action 
	 */
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
		gui.enableSystemIdentificationPanel();
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
			gui.disableSystemIdentificationPanel();
            gui.notConnectedToCloudProvider();
            stopController();
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

	public void actuate(double controlInput, int numberOfNodes) {
		if (connectedToCloudProvider)
			trigger(new NodeRequest(cloudProviderAddress, controlInput, numberOfNodes), actuatorChannel);
		else
			logger.warn("You should first connect to cloud provider");
	}
	
}
