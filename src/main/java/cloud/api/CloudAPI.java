package cloud.api;

import cloud.api.address.AddressManager;
import cloud.common.*;
import cloud.elb.NodesToRemove;
import cloud.epfd.HealthCheckerInit;
import cloud.gui.CloudGUI;
import cloud.requestengine.ResponseTimeService;
import econtroller.controller.*;
import instance.Node;
import instance.common.InstanceStarted;
import instance.common.ShutDown;
import instance.os.InstanceCost;
import logger.Logger;
import logger.LoggerFactory;
import scenarios.manager.CloudConfiguration;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
    private boolean headLess;
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
    private ConcurrentHashMap<String, Double> costTable = new ConcurrentHashMap<String, Double>();
    private ConcurrentHashMap<String, Double> periodicCostTable = new ConcurrentHashMap<String, Double>();

	private int numberOfInstances = 0;

	public CloudAPI() {
		api = this;
		subscribe(initHandler, control);

		subscribe(suspectHandler, epfd);
		subscribe(restoreHandler, epfd);

		subscribe(replicasHandler, elb);
		subscribe(rebalanceResponseHandler, elb);
        subscribe(nodesToRemoveHandler, elb);

		subscribe(instanceStartedHandler, network);
		subscribe(connectControllerHandler, network);
		subscribe(disconnectHandler, network);
		subscribe(newNodeRequestHandler, network);
		subscribe(instanceCostHandler, network);
		subscribe(requestTrainingDataHandler, network);
		subscribe(requestSensingData, network);
		subscribe(removeNodeHandler, network);
	}

    Handler<CloudAPIInit> initHandler = new Handler<CloudAPIInit>() {
		@Override
		public void handle(CloudAPIInit event) {
			setupGui(event);
			logger.info("CloudAPI component started...");
			cloudConfiguration = event.getCloudConfiguration();
			self = event.getSelf();
            headLess = event.getCloudConfiguration().isHeadLess();
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
			gui.instanceStarted(event.getNode());
			gui.instanceAdded();
			currentNodes.add(event.getNode());
			logger.info("Node " + event.getNode() + " initialized   [ok]");
			trigger(new ConsiderInstance(event.getNode()), epfd);
			if (connectedToController) trigger(new NewNodeToMonitor(self, controllerAddress, event.getNode().getAddress()), network);
		}
	};
	
	/**
	 * This handler informs ELB for a suspected node
	 */
	Handler<Suspect> suspectHandler = new Handler<Suspect>() {
		@Override
		public void handle(Suspect event) {
			Node node = dnsService.getNodeForAddress(event.node());
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
			Node node = dnsService.getNodeForAddress(event.node());
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
			logger.debug("Received '" + event.nodeConfiguration().getBlocks().size() + "' Block(s) from ELB for node " + event.nodeConfiguration().getNode());
            event.nodeConfiguration().setHeadLess(headLess);
			instanceManagement.initializeNode(event.nodeConfiguration());
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
            logger.info("Received request to launch " + event.numberOfNodes() + "new instance(s)");
            for (Integer i=0; i<event.numberOfNodes(); i++) {
                NodeConfiguration nodeConfiguration = new NodeConfiguration();
                logger.debug("nodeConfiguration: \n" + nodeConfiguration);
                Node node = getNewNodeInfo();
                nodeConfiguration.setNodeInfo(node);
                gui.addNewInstance(node);
                numberOfInstances++;
                trigger(new RebalanceDataBlocks(nodeConfiguration), elb);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
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
            event.getNodeConfiguration().setHeadLess(headLess);
			instanceManagement.initializeNode(event.getNodeConfiguration());
		}
	};
	
	/**
	 * This handler is triggered when a node sends its cost to cloudProvider
	 */
	Handler<InstanceCost> instanceCostHandler = new Handler<InstanceCost>() {
		@Override
		public void handle(InstanceCost event) {
			gui.updateCostForNode(event.getNode(), event.getTotalCost());
			costTable.put(event.getNode().getNodeName(), Double.parseDouble(event.getTotalCost()));
			periodicCostTable.put(event.getNode().getNodeName(), Double.parseDouble(event.getPeriodicCost()));
		}
	};
	
	/**
	 * This handler is triggered when the modeler requests training data from cloudAPI.
	 * It distributes this request to corresponding components 
	 */
	Handler<RequestTrainingData> requestTrainingDataHandler = new Handler<RequestTrainingData>() {
		@Override
		public void handle(RequestTrainingData event) {
			collectData(event.getSource(), true);			
		}
	};

	/**
	 * This handler is triggered when the sensor request data to sense. This data will be used by the
	 * controller to act accordingly.
	 */
	Handler<RequestSensingData> requestSensingData = new Handler<RequestSensingData>() {
		@Override
		public void handle(RequestSensingData event) {
			collectData(event.getSource(), false);
		}
	};
	
	/**
	 * This handler is triggered when the modeler from controller request to remove a node
	 */
	Handler<RemoveNode> removeNodeHandler = new Handler<RemoveNode>() {
		@Override
		public void handle(RemoveNode event) {
			if (!currentNodes.isEmpty()) {
                trigger(new SelectNodesToRemove(currentNodes, event.numberOfNodes()), elb);
			} else {
                logger.error("No nodes left to kill!");
            }
		}
	};

    /**
     * This handler is triggered when the ELB returns the nodes to remove for CloudAPI
     */
    Handler<NodesToRemove> nodesToRemoveHandler = new Handler<NodesToRemove>() {
        @Override
        public void handle(NodesToRemove event) {
            if (!currentNodes.isEmpty()) {
                for (Node node : event.nodes()) {
                    logger.warn("Killing node " + node);
                    gui.removeNodeFromCurrentInstances(node);
                    kill(node);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        logger.error("Error while trying to sleep between the kills");
                    }
                }
            }
        }
    };
	
	private void collectData(Address source, boolean isTrainingData) {
		SendRawData data = new SendRawData(source, numberOfInstances, isTrainingData);
        double totalCost = calculateTotalCost();
		double periodicTotalCost = calculatePeriodicTotalCost();
		data.setPeriodicTotalCost(periodicTotalCost);
        data.setTotalCost(totalCost);
        gui.updateTotalCost(totalCost);
		trigger(data, elb);
	}

    private double calculateTotalCost() {
        double cost = 0;
        for (String node : costTable.keySet())
            cost += costTable.get(node);
        return cost;
    }
	
	private double calculatePeriodicTotalCost() {
		double cost = 0;
		for (String node : periodicCostTable.keySet())
			cost += periodicCostTable.get(node);
		return cost;
	}
	
	public void kill(Node node) {
		numberOfInstances--;
		logger.debug("Shutting down Node " + node);
		logger.debug("Number of instances: " + numberOfInstances);
		trigger(new ShutDown(self, node.getAddress()), network);
		trigger(new InstanceKilled(node), epfd);
		trigger(new RemoveReplica(node), elb);
		instanceManagement.kill(node);	
		addressManager.releaseAddress(node.getAddress());
		currentNodes.remove(node);
	}

	protected void startElasticLoadBalancer() {
		trigger(new ELBInit(cloudConfiguration.getBlocks(), cloudConfiguration.getReplicationDegree(), self, cloudConfiguration.getSla()), elb);
	}

	public void initialize(NodeConfiguration nodeConfiguration, boolean alreadyDefined) {
		if (!alreadyDefined) {
			Node node = getNewNodeInfo();
            gui.addNewInstance(node);
            numberOfInstances++;
			nodeConfiguration.setNodeInfo(node);
		}
		if (currentNodes.size() != 0) {
			trigger(new RebalanceDataBlocks(nodeConfiguration), elb);
		} else {
			trigger(new GetReplicas(nodeConfiguration), elb);
			logger.debug("Requesting replicas from ELB...");
			dnsService.addDNSEntry(nodeConfiguration.getNode());
		}
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
