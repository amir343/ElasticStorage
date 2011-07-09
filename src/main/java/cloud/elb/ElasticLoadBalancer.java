package cloud.elb;

import instance.Node;
import instance.common.Block;
import instance.common.BlocksAck;
import instance.common.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;
import cloud.api.RebalanceDataBlocks;
import cloud.api.RebalanceResponseMap;
import cloud.common.ELB;
import cloud.common.ELBInit;
import cloud.common.Generator;
import cloud.common.GetReplicas;
import cloud.common.NodeConfiguration;
import cloud.common.RemoveReplica;
import cloud.common.Replicas;
import cloud.common.RequestMessage;
import cloud.common.RestoreNode;
import cloud.common.SendRawData;
import cloud.common.SuspectNode;
import cloud.common.TrainingData;
import cloud.gui.CloudGUI;
import cloud.requestengine.DownloadStarted;
import cloud.requestengine.RequestGeneratorInit;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-28
 *
 */

public class ElasticLoadBalancer extends ComponentDefinition {


	private Logger logger = LoggerFactory.getLogger(ElasticLoadBalancer.class, CloudGUI.getInstance());
	
	// Ports
	Negative<ELB> elb = provides(ELB.class);
	Positive<Generator> generator = requires(Generator.class);
	Positive<Network> network = requires(Network.class);
	Positive<Timer> timer = requires(Timer.class);

	private LeastCPULoadAlgorithm loadBalancerAlgorithm = LeastCPULoadAlgorithm.getInstance();
	private ELBTable elbTable;
	protected Address self;
	private static final long ELB_TREE_UPDATE_INTERVAL = 30000;
	private Map<Address, Double> cpuLoads = new HashMap<Address, Double>();
	private Map<Address, Long> bandwidths = new HashMap<Address, Long>();

	public ElasticLoadBalancer() {
		subscribe(initHandler, elb);
		subscribe(getReplicasHandler, elb);
		subscribe(suspectNodeHandler, elb);
		subscribe(restoreNodeHandler, elb);
		subscribe(removeReplicaHandler, elb);
		subscribe(rebalanceDataBlocksHandler, elb);
		subscribe(sendRawDataHandler, elb);
		
		subscribe(requestHandler, generator);
		subscribe(responseTimeHandler, generator);

		subscribe(blocksAckHandler, network);
		subscribe(downloadStartedHandler, network);
		subscribe(myCPULoadHandler, network);
		subscribe(activateBlockHandler, network);
		
		subscribe(elbTreeUpdateHandler, timer);
	}

	Handler<ELBInit> initHandler = new Handler<ELBInit>() {
		@Override
		public void handle(ELBInit event) {
			startELBTable(event);
			self = event.getSelf();
			trigger(new RequestGeneratorInit(), generator);
			logger.info("Elastic Load Balancer is started...");
			scheduleUpdateELBTree();
		}
	};
	
	/**
	 * This handler is triggered by CloudAPI upon launching a new instance
	 */
	Handler<GetReplicas> getReplicasHandler = new Handler<GetReplicas>() {
		@Override
		public void handle(GetReplicas event) {
			logger.debug("Preparing Replicas....");
			List<Block> blocks = elbTable.prepareBlocksForNode(event.getNodeConfiguration().getNode());
			NodeConfiguration nodeConfiguration = event.getNodeConfiguration();
			nodeConfiguration.setBlocks(blocks);
			trigger(new Replicas(nodeConfiguration), elb);
			logger.debug(nodeConfiguration.getBlocks().size() + " Replicas created! ");
		}
	};
	
	/**
	 * This handler is triggered when receives a suspect signal from EPFD so it marks the corresponding data blocks' replica as suspected 
	 */
	Handler<SuspectNode> suspectNodeHandler = new Handler<SuspectNode>() {
		@Override
		public void handle(SuspectNode event) {
			elbTable.suspectEntriesForNode(event.getNode());
		}
	};
	
	/**
	 * This handler is triggered when receives a restore signal from EPFD so it marks the corresponding data blocks' replica as restored
	 */
	Handler<RestoreNode> restoreNodeHandler = new Handler<RestoreNode>() {
		@Override
		public void handle(RestoreNode event) {
			elbTable.restoreEntriesForNode(event.getNode());			
		}
	};
	
	/**
	 * This handler is triggered when a node shuts down so its replicas become unavailable
	 */
	Handler<RemoveReplica> removeReplicaHandler = new Handler<RemoveReplica>() {
		@Override
		public void handle(RemoveReplica event) {
			elbTable.removeReplicasForNode(event.getNode());
		}
	};
	
	/**
	 * This handler is triggered when the newly requested instance starts up completely and acknowledges back its start up so its data blocks' replica
	 * become available
	 */
	Handler<BlocksAck> blocksAckHandler = new Handler<BlocksAck>() {
		@Override
		public void handle(BlocksAck event) {
			elbTable.activeBlocksForNode(event.getNode());
			trigger(new BlocksActivated(elbTable.getblocks()), generator);
		}
	};
	
	/**
	 * This handler is triggered when the newly joined instance informs about a block that is available and can be served from that instance
	 */
	Handler<ActivateBlock> activateBlockHandler = new Handler<ActivateBlock>() {
		@Override
		public void handle(ActivateBlock event) {
			elbTable.activateBlockForNode(event.getNode(), event.getBlock());
			BlocksActivated blocksActivated = new BlocksActivated();
			blocksActivated.addBlock(event.getBlock());
			trigger(blocksActivated, generator);
		}
		
	};
	
	/**
	 * This handler is triggered by Request Generator and it is responsible for sending
	 * request to the chosen node with respect to LoadBalancerAlgorithm
	 */
	Handler<Request> requestHandler = new Handler<Request>() {
		@Override
		public void handle(Request event) {
			Node node = elbTable.getNextNodeToSendThisRequest(event.getBlockId());
			if (node != null) {
				loadBalancerAlgorithm.increaseNrOfSentRequestFor(node);
				logger.debug("Will send to " + node);
				trigger(new RequestMessage(self, node.getAddress(), event), network);
			} else
				logger.error("No node found to send the next request in elbTable");
		}
	};
	
	/**
	 * This handler is triggered when a transfer finishes
	 */
	Handler<DownloadStarted> downloadStartedHandler = new Handler<DownloadStarted>() {
		@Override
		public void handle(DownloadStarted event) {
			trigger(event, generator);
		}
	};
	
	/**
	 * This handler receives the cpu load from an instance and updates the 
	 * nodeStatistics in LoadBalancerAlgorithm 
	 */
	Handler<MyCPULoadAndBandwidth> myCPULoadHandler = new Handler<MyCPULoadAndBandwidth>() {
		@Override
		public void handle(MyCPULoadAndBandwidth event) {
			loadBalancerAlgorithm.updateCPULoadFor(event.getNode(), event.getCpuLoad());
			cpuLoads.put(event.getSource(), event.getCpuLoad());
			bandwidths.put(event.getSource(), event.getCurrentBandwidth());
		}
	};
	
	/**
	 * This handler is triggered when API requests data blocks map according to least CPU load so the 
	 * new instance can retrieve each data block from an existing instance
	 */
	Handler<RebalanceDataBlocks> rebalanceDataBlocksHandler = new Handler<RebalanceDataBlocks>() {
		@Override
		public void handle(RebalanceDataBlocks event) {
			Map<String, Address> dataBlocksMap = new HashMap<String, Address>();
			for (ELBEntry entry : elbTable.getEntries()) {
				Node node = loadBalancerAlgorithm.getNextNodeFrom(entry.getReplicas());
				dataBlocksMap.put(entry.getBlock().getName(), node.getAddress());
			}
			NodeConfiguration nodeConfig = event.getNodeConfiguration();
			nodeConfig.setDataBlocksMap(dataBlocksMap);
			trigger(new RebalanceResponseMap(nodeConfig), elb);
		}
	};
	
	/**
	 * This handler is triggered when ELB receives a request from cloudProvider to send the training data to controller
	 */
	Handler<SendRawData> sendRawDataHandler = new Handler<SendRawData>() {
		@Override
		public void handle(SendRawData event) {
			trigger(event, generator);
		}
	};
	
	/**
	 * This handler is triggered when RequestGenerator provides the average response time
	 */
	Handler<SendRawData> responseTimeHandler = new Handler<SendRawData>() {
		@Override
		public void handle(SendRawData event) {
			if (event.isTrainingData()) {
				TrainingData data = new TrainingData(self, event.getController(), event.getNrNodes());
				data.setResponseTimeMean(event.getAverageResponseTime());
				data.setThroughputMean(event.getAverageThroughput());
				data.setCpuLoalMean(calculateCPULoadMean());
				data.setBandwidthMean(calculateBandwidthMean());
				data.setTotalCost(event.getTotalCost());
				trigger(data, network);
			} else {
				SenseData data = new SenseData(self, event.getController(), event.getNrNodes());
				data.setResponseTimeMean(event.getAverageResponseTime());
				data.setThroughputMean(event.getAverageThroughput());
				data.setCpuLoalMean(calculateCPULoadMean());
				data.setBandwidthMean(calculateBandwidthMean());
				data.setTotalCost(event.getTotalCost());
				trigger(data, network);
			}
		}
	};
	
	/**
	 * This handler is triggered when timer times out and it should update the ELB tree
	 */
	Handler<UpdateELBTree> elbTreeUpdateHandler = new Handler<UpdateELBTree>() {
		@Override
		public void handle(UpdateELBTree event) {
			CloudGUI.getInstance().updateTree(elbTable);
			scheduleUpdateELBTree();
		}
	};

	private double calculateBandwidthMean() {
		if (bandwidths.size() == 0) return 0.0;
		Long sum = 0L;
		double average = 0.0;
		synchronized (bandwidths) {
			for (Address node: bandwidths.keySet())
				sum += bandwidths.get(node);
			average = sum.doubleValue()/bandwidths.size();
		}
		return average;
	}
	
	protected void scheduleUpdateELBTree() {
		ScheduleTimeout st = new ScheduleTimeout(ELB_TREE_UPDATE_INTERVAL);
		st.setTimeoutEvent(new UpdateELBTree(st));
		trigger(st, timer);
	}

	private double calculateCPULoadMean() {
		if (cpuLoads.size() == 0) return 0.0;
		double sum = 0.0;
		double average = 0.0;
		synchronized (cpuLoads) {
			for (Address node: cpuLoads.keySet())
				sum += cpuLoads.get(node);
			average = sum/cpuLoads.size();
		}
		return average;
	}
	
	protected void startELBTable(ELBInit event) {
		elbTable = new ELBTable(event.getReplicationDegree());
		for (Block block : event.getBlocks()) {
			ELBEntry entry = new ELBEntry(block.getName(), block.getSize());
			elbTable.addEntry(entry);
		}
	}
	
}
