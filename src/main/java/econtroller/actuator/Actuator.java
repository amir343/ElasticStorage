package econtroller.actuator;

import econtroller.ControllerConfiguration;
import econtroller.controller.NewNodeRequest;
import econtroller.controller.RemoveNode;
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

    protected ControllerConfiguration controllerConfiguration;
	protected Address self;
    private int MINIMUM_NUMBER_OF_NODES = 3;

	public Actuator() {
		subscribe(initHandler, control);
		
		subscribe(nodeRequestHandler, actuatorChannel);
	}
	
	Handler<ActuatorInit> initHandler = new Handler<ActuatorInit>() {
		@Override
		public void handle(ActuatorInit event) {
			controllerConfiguration = event.getControllerConfiguration();
			self = controllerConfiguration.getSelfAddress();
			logger.info("Actuator component is stared.");
		}
	};

    /**
	 * This handler is triggered when it receives a signal from controller in order to request a new node
	 */
	Handler<NodeRequest> nodeRequestHandler = new Handler<NodeRequest>() {
		@Override
		public void handle(NodeRequest event) {
            Address cloudProviderAddress = event.cloudProviderAddress();
            int controlInput = (int) event.controlInput();
            if (controlInput <= MINIMUM_NUMBER_OF_NODES) controlInput = MINIMUM_NUMBER_OF_NODES;
            int diff = controlInput - event.numberOfNodes();
            if (diff > 0 ) {
                logger.info("Request to launch " + diff + " node(s)");
                trigger(new NewNodeRequest(self, cloudProviderAddress, diff), network);
            }
            else if (diff < 0 ) {
                diff = Math.abs(diff);
                logger.info("Request to remove " + diff + " node(s)");
                trigger(new RemoveNode(self, cloudProviderAddress, diff), network);
            }

		}
	};
	
}
