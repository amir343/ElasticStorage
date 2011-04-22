package cloud.api;

import instance.Node;
import instance.common.InstanceStarted;
import instance.common.ShutDown;
import instance.common.ShutDownAck;

import java.util.ArrayList;
import java.util.List;

import logger.Logger;
import logger.LoggerFactory;
import scenarios.manager.CloudConfiguration;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import cloud.api.adress.AddressManager;
import cloud.common.CloudAPIInit;
import cloud.common.ConsiderInstance;
import cloud.common.ELB;
import cloud.common.ELBInit;
import cloud.common.EPFD;
import cloud.common.GetReplicas;
import cloud.common.InstanceKilled;
import cloud.common.NodeConfiguration;
import cloud.common.RemoveReplica;
import cloud.common.Replicas;
import cloud.common.Restore;
import cloud.common.RestoreNode;
import cloud.common.Suspect;
import cloud.common.SuspectNode;
import cloud.epfd.HealthCheckerInit;
import cloud.gui.CloudGUI;
import cloud.requestengine.ResponseTimeService;
import econtroller.controller.Connect;
import econtroller.controller.ConnectionEstablished;
import econtroller.controller.Disconnect;
import econtroller.controller.NewNodeRequest;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-23
 *
 */

public class CloudAPI extends ComponentDefinition {

	protected Logger logger = LoggerFactory.getLogger(CloudAPI.class, CloudGUI.getInstance());

	// Ports
	Positive<Timer> timer = requires(Timer.class);
	Positive<Network> network = requires(Network.class);
	Positive<EPFD> epfd = requires(EPFD.class);
	Positive<ELB> elb = requires(ELB.class);

	// Services
	private InstanceManagement instanceManagement;
	private AddressManager addressManager;
	protected CloudGUI gui;
	protected Address self;
	private CloudAPI api;
	private int lastCreatedElasticStorageNode = 1;
	protected CloudConfiguration cloudConfiguration;
	private DNSService dnsService = new DNSService();
	private int lastCreatedSnapshotId = 1;
	protected Address controllerAddress;
	protected boolean connectedToController = false;
	private List<Node> currentNodes = new ArrayList<Node>();
	
	public CloudAPI() {
		api = this;
		subscribe(initHandler, control);
		
		subscribe(suspectHandler, epfd);
		subscribe(restoreHandler, epfd);
		
		subscribe(replicasHandler, elb);
		subscribe(rebalanceResponseHandler, elb);
		
		subscribe(instanceStartedHandler, network);
		subscribe(connectControllerHandler, network);
		subscribe(disconnectHandler, network);
		subscribe(newNodeRequestHandler, network);
	}
	
	Handler<CloudAPIInit> initHandler = new Handler<CloudAPIInit>() {
		@Override
		public void handle(CloudAPIInit event) {
			setupGui(event);
			logger.info("CloudAPI component started...");
			cloudConfiguration = event.getCloudConfiguration();
			self = event.getSelf();
			startElasticLoadBalancer();
			setupServices(event);
			setupHealthChecker(event);
		}
	};
	
	/**
	 * This handler is triggered when the new instance sends back that is ready to be considered in the instance group
	 */
	Handler<InstanceStarted> instanceStartedHandler = new Handler<InstanceStarted>() {
		@Override
		public void handle(InstanceStarted event) {
			gui.addNewInstance(event.getNode());		
			gui.instanceAdded();
			currentNodes.add(event.getNode());
			logger.info("Node " + event.getNode() + " initialized   [ok]");
			trigger(new ConsiderInstance(event.getNode()), epfd);
			if (connectedToController) trigger(new NewNodeToMonitor(self, controllerAddress, event.getNode().getAddress()), network);
		}
	};
	
	Handler<ShutDownAck> shutDownAckHandler = new Handler<ShutDownAck>() {
		@Override
		public void handle(ShutDownAck event) {
			gui.instanceRemoved();
		}
	};
	
	/**
	 * This handler informs ELB for a suspected node
	 */
	Handler<Suspect> suspectHandler = new Handler<Suspect>() {
		@Override
		public void handle(Suspect event) {
			Node node = dnsService.getNodeForAddress(event.getNode());
			if (node != null) {
				trigger(new SuspectNode(node), elb);
				gui.suspectInstance(node);
			}
		}
	};
	
	/**
	 * This handler informs ELB for a restored node
	 */
	Handler<Restore> restoreHandler = new Handler<Restore>() {
		@Override
		public void handle(Restore event) {
			Node node = dnsService.getNodeForAddress(event.getNode());
			if (node != null) {
				trigger(new RestoreNode(node), elb);
				gui.restoreInstance(node);
			}
		}
	};
	
	/**
	 * This is the handler that is triggered by ELB and it sends back a set of
	 * blocks that the node should start with
	 */
	Handler<Replicas> replicasHandler = new Handler<Replicas>() {
		@Override
		public void handle(Replicas event) {
			logger.debug("Received '" + event.getNodeConfiguration().getBlocks().size() + "' Block(s) from ELB for node " + event.getNodeConfiguration().getNode());
			instanceManagement.initializeNode(event.getNodeConfiguration());
		}
	};

	/**
	 * This handler is triggered when the controller tries to connect to cloud API 
	 */
	Handler<Connect> connectControllerHandler = new Handler<Connect>() {
		@Override
		public void handle(Connect event) {
			controllerAddress = event.getSource();
			connectedToController = true;
			logger.warn("Controller connected to Cloud API");
			List<Address> nodes = instanceManagement.getAllNodes();
			trigger(new ConnectionEstablished(self, controllerAddress, nodes), network);
		}
	};
	
	/**
	 * This handler is triggered when the controller disconnects from the cloud API
	 */
	Handler<Disconnect> disconnectHandler = new Handler<Disconnect>() {
		@Override
		public void handle(Disconnect event) {
			connectedToController = false;
			logger.warn("Controller disconnected from Cloud API");
		}
	};
	
	/**
	 * This handler is triggered by controller
	 */
	Handler<NewNodeRequest> newNodeRequestHandler = new Handler<NewNodeRequest>() {
		@Override
		public void handle(NewNodeRequest event) {
			NodeConfiguration nodeConfiguration = new NodeConfiguration();
			Node node = getNewNodeInfo();
			nodeConfiguration.setNodeInfo(node);
			trigger(new RebalanceDataBlocks(nodeConfiguration), elb);
		}
	};
	
	/**
	 * This handler is triggered when ELB provides the Cloud API with the map of data blocks that the newly joined 
	 * node can retrieve data blocks from
	 */
	Handler<RebalanceResponseMap> rebalanceResponseHandler = new Handler<RebalanceResponseMap>() {
		@Override
		public void handle(RebalanceResponseMap event) {
			dnsService.addDNSEntry(event.getNodeConfiguration().getNode());
			instanceManagement.initializeNode(event.getNodeConfiguration());
		}
	};

	public void kill(Node node) {
		trigger(new ShutDown(self, node.getAddress()), network);
		logger.debug("Shuting down Node " + node);
		trigger(new InstanceKilled(node), epfd);
		trigger(new RemoveReplica(node), elb);
		instanceManagement.kill(node);	
		addressManager.releaseAddress(node.getAddress());
		currentNodes.remove(node);
	}

	protected void startElasticLoadBalancer() {
		trigger(new ELBInit(cloudConfiguration.getBlocks(), cloudConfiguration.getReplicationDegree(), self), elb);
	}

	public void initialize(NodeConfiguration nodeConfiguration, boolean alreadyDefined) {
		if (!alreadyDefined) {
			Node node = getNewNodeInfo();
			nodeConfiguration.setNodeInfo(node);
		}
		trigger(new GetReplicas(nodeConfiguration), elb);
		logger.debug("Requesting replicas from ELB...");
		dnsService.addDNSEntry(nodeConfiguration.getNode());
	}

	private Node getNewNodeInfo() {
		Address address = addressManager.getAFreeAddress();
		String name = "ElasticStorage" + lastCreatedElasticStorageNode;
		lastCreatedElasticStorageNode++;
		Node node = new Node(name, address.getIp().getHostAddress(), address.getPort());
		return node;
	}
	
	private void setupHealthChecker(CloudAPIInit event) {
		HealthCheckerInit init = new HealthCheckerInit(event.getPeriod(), event.getDelta(), self);
		trigger(init, epfd);			
	}

	private void setupServices(CloudAPIInit event) {
		addressManager = new AddressManager(cloudConfiguration.getAddressPollXmlFilename());
		logger.info(addressManager.getNrOfAvailableAddress() + " elastic IPs are available");
		instanceManagement = new InstanceManagement(event.getCloudConfiguration(), logger);
		for (NodeConfiguration nodeConfiguration : event.getCloudConfiguration().getNodeConfigurations()) 
			initialize(nodeConfiguration, true);
	}

	private void setupGui(CloudAPIInit event) {
		gui = CloudGUI.getInstance();
		gui.setTitle("CloudProvider@" + event.getCloudConfiguration().getCloudProviderAddress() + ":" + event.getCloudConfiguration().getCloudProviderPort());
		gui.setCloudProviderAPI(api);
	}

	public void takeSnapshot() {
		CloudSnapshot cloudSnapshot = new CloudSnapshot(lastCreatedSnapshotId++); 
		cloudSnapshot.setResponseTimeChart(ResponseTimeService.getInstance().getChart());
		gui.addSnapshot(cloudSnapshot);
	}

	public void restartInstance(String address) {
		Address instance = Node.getAddressFromString(address);
		trigger(new RestartInstance(self, instance), network);
	}

}
