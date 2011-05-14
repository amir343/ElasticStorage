package cloud.elb;

import instance.Node;
import instance.common.Block;
import instance.common.BlocksAck;
import instance.common.Request;

import java.util.ArrayList;
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
import cloud.requestengine.RequestDone;
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

	private LeastCPULoadAlgorithm loadBalancerAlgorithm = LeastCPULoadAlgorithm.getInstance();
	private ELBTable elbTable;
	protected Address self;
	
	private List<Double> cpuLoads = new ArrayList<Double>();
	private List<Long> bandwidths = new ArrayList<Long>();

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
		subscribe(requestDoneHandler, network);
		subscribe(myCPULoadHandler, network);
		subscribe(activateBlockHandler, network);
	}

	Handler<ELBInit> initHandler = new Handler<ELBInit>() {
		@Override
		public void handle(ELBInit event) {
			startELBTable(event);
			self = event.getSelf();
			trigger(new RequestGeneratorInit(), generator);
			logger.info("Elastic Load Balancer is started...");
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
			}
		}
	};
	
	/**
	 * This handler is triggered when a transfer finishes
	 */
	Handler<RequestDone> requestDoneHandler = new Handler<RequestDone>() {
		@Override
		public void handle(RequestDone event) {
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
			cpuLoads.add(event.getCpuLoad());
			bandwidths.add(event.getCurrentBandwidth());
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
			TrainingData data = new TrainingData(self, event.getController(), event.getNrNodes());
			
			data.setResponseTimeMean(event.getAverageResponseTime());
			data.setThroughputMean(event.getAverageThroughput());
			data.setCpuLoalMean(calculateCPULoadMean());
			data.setBandwidthMean(calculateBandwidthMean());
			data.setTotalCost(event.getTotalCost());
			
			trigger(data, network);
		}
	};

	private double calculateBandwidthMean() {
		if (bandwidths.size() == 0) return 0.0;
		Long mean = 0L;
		for (Long band : bandwidths)
			mean += band;
		return mean.doubleValue()/bandwidths.size();
	}
	
	private double calculateCPULoadMean() {
		if (cpuLoads.size() == 0) return 0.0;
		double mean = 0.0;
		for (double load : cpuLoads)
			mean += load;
		return mean/cpuLoads.size();
	}
	
	protected void startELBTable(ELBInit event) {
		elbTable = new ELBTable(event.getReplicationDegree());
		for (Block block : event.getBlocks()) {
			ELBEntry entry = new ELBEntry(block.getName(), block.getSize());
			elbTable.addEntry(entry);
		}
	}
	
}
