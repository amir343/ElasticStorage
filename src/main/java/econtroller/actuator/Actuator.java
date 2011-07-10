package econtroller.actuator;

import econtroller.ControllerConfiguration;
import econtroller.controller.NewNodeRequest;
import econtroller.gui.ControllerGUI;
import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-23
 *
 */

public class Actuator extends ComponentDefinition {
	
	private Logger logger = LoggerFactory.getLogger(Actuator.class, ControllerGUI.getInstance());
	
	Positive<Network> network = requires(Network.class);
	Negative<ActuatorChannel> actuatorChannel = provides(ActuatorChannel.class);

	protected ControllerConfiguration controllerConfiguaration;
	protected Address self;

	public Actuator() {
		subscribe(initHandler, control);
		
		subscribe(nodeRequestHandler, actuatorChannel);
	}
	
	Handler<ActuatorInit> initHandler = new Handler<ActuatorInit>() {
		@Override
		public void handle(ActuatorInit event) {
			controllerConfiguaration = event.getControllerConfiguration();
			self = controllerConfiguaration.getSelfAddress();
			logger.info("Actuator component is stared.");
		}
	};
	
	/**
	 * This handler is triggered when it receives a signal from controller in order to request a new node
	 */
	Handler<NodeRequest> nodeRequestHandler = new Handler<NodeRequest>() {
		@Override
		public void handle(NodeRequest event) {
			Address cloudProviderAddress = event.getCloudProviderAddress();
			trigger(new NewNodeRequest(self, cloudProviderAddress), network);	
		}
	};
	
}
