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
import cloud.common.SuspectNode;
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

	public ElasticLoadBalancer() {
		subscribe(initHandler, elb);
		subscribe(getReplicasHandler, elb);
		subscribe(suspectNodeHandler, elb);
		subscribe(restoreNodeHandler, elb);
		subscribe(removeReplicaHandler, elb);
		subscribe(rebalanceDataBlocksHandler, elb);
		
		subscribe(requestHandler, generator);

		subscribe(blocksAckHandler, network);
		subscribe(requestDoneHandler, network);
		subscribe(myCPULoadHandler, network);
	}

	Handler<ELBInit> initHandler = new Handler<ELBInit>() {
		@Override
		public void handle(ELBInit event) {
			startELBTable(event);
			self = event.getSelf();
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
	
	Handler<SuspectNode> suspectNodeHandler = new Handler<SuspectNode>() {
		@Override
		public void handle(SuspectNode event) {
			elbTable.suspectEntriesForNode(event.getNode());
		}
	};
	
	Handler<RestoreNode> restoreNodeHandler = new Handler<RestoreNode>() {
		@Override
		public void handle(RestoreNode event) {
			elbTable.restoreEntriesForNode(event.getNode());			
		}
	};
	
	Handler<RemoveReplica> removeReplicaHandler = new Handler<RemoveReplica>() {
		@Override
		public void handle(RemoveReplica event) {
			elbTable.removeReplicasForNode(event.getNode());
		}
	};
	
	Handler<BlocksAck> blocksAckHandler = new Handler<BlocksAck>() {
		@Override
		public void handle(BlocksAck event) {
			elbTable.activeBlocksForNode(event.getNode());
			trigger(new RequestGeneratorInit(elbTable.getblocks()), generator);
		}
	};
	
	/**
	 * This handler is triggered by Request Generator and it is responsible for sending
	 * request to the choosen node with respect to LoadBalancerAlgorithm
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
	Handler<MyCPULoad> myCPULoadHandler = new Handler<MyCPULoad>() {
		@Override
		public void handle(MyCPULoad event) {
			loadBalancerAlgorithm.updateCPULoadFor(event.getNode(), event.getCpuLoad());
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

	protected void startELBTable(ELBInit event) {
		elbTable = new ELBTable(event.getReplicationDegree());
		for (Block block : event.getBlocks()) {
			ELBEntry entry = new ELBEntry(block.getName(), block.getSize());
			elbTable.addEntry(entry);
		}
	}
	
}
