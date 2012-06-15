/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cloud.elb;

import cloud.api.RebalanceDataBlocks;
import cloud.api.RebalanceResponseMap;
import cloud.common.*;
import cloud.gui.CloudGUI;
import cloud.requestengine.DownloadStarted;
import cloud.requestengine.RequestGeneratorInit;
import instance.Node;
import instance.common.Block;
import instance.common.BlocksAck;
import instance.common.Rejected;
import instance.common.Request;
import logger.Logger;
import logger.LoggerFactory;
import scenarios.manager.SLA;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import javax.swing.plaf.synth.SynthLookAndFeel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final Map<Address, Double> cpuLoads = new HashMap<Address, Double>();
	private final Map<Address, Long> bandwidths = new HashMap<Address, Long>();
    private boolean slaEnabled = false;
    private SLA sla;

	public ElasticLoadBalancer() {
		subscribe(initHandler, elb);
		subscribe(getReplicasHandler, elb);
		subscribe(suspectNodeHandler, elb);
		subscribe(restoreNodeHandler, elb);
		subscribe(removeReplicaHandler, elb);
		subscribe(rebalanceDataBlocksHandler, elb);
		subscribe(sendRawDataHandler, elb);
        subscribe(selectNodesToRemoveHandler, elb);
		
		subscribe(requestHandler, generator);
		subscribe(responseTimeHandler, generator);

		subscribe(blocksAckHandler, network);
		subscribe(downloadStartedHandler, network);
		subscribe(myCPULoadHandler, network);
		subscribe(activateBlockHandler, network);
        subscribe(rejectedResponseHandler, network);
		
		subscribe(elbTreeUpdateHandler, timer);
	}

	Handler<ELBInit> initHandler = new Handler<ELBInit>() {
		@Override
		public void handle(ELBInit event) {
			startELBTable(event);
			self = event.self();
			trigger(new RequestGeneratorInit(), generator);
			logger.info("Elastic Load Balancer is started...");
            getSLA(event);
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
			List<Block> blocks = elbTable.prepareBlocksForNode(event.nodeConfiguration().getNode());
			NodeConfiguration nodeConfiguration = event.nodeConfiguration();
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
			elbTable.suspectEntriesForNode(event.node());
		}
	};

	/**
	 * This handler is triggered when receives a restore signal from EPFD so it marks the corresponding data blocks' replica as restored
	 */
	Handler<RestoreNode> restoreNodeHandler = new Handler<RestoreNode>() {
		@Override
		public void handle(RestoreNode event) {
			elbTable.restoreEntriesForNode(event.node());
		}
	};

	/**
	 * This handler is triggered when a node shuts down so its replicas become unavailable
	 */
	Handler<RemoveReplica> removeReplicaHandler = new Handler<RemoveReplica>() {
		@Override
		public void handle(RemoveReplica event) {
			elbTable.removeReplicasForNode(event.node());
            loadBalancerAlgorithm.nodeRemoved(event.node());
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
			elbTable.activateBlockForNode(event.node(), event.block());
			BlocksActivated blocksActivated = new BlocksActivated();
			blocksActivated.addBlock(event.block());
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
                logger.warn("Sending request for '" + event.getBlockId() + "' to " + node);
				trigger(new RequestMessage(self, node.getAddress(), event), network);
			} else
				logger.error("No node found to send the next request in elbTable");
		}
	};

	/**
	 * This handler is triggered when a transfer starts
	 */
	Handler<DownloadStarted> downloadStartedHandler = new Handler<DownloadStarted>() {
		@Override
		public void handle(DownloadStarted event) {
			trigger(event, generator);
		}
	};

     /**
     * This handler is triggered when a transfer is rejected since there is no free slot in the node
     */
     Handler<Rejected> rejectedResponseHandler = new Handler<Rejected>() {
          @Override
          public void handle(Rejected event) {
               trigger(new DownloadRejected(event.getRequest()), generator);
          }
          };

     /**
      * This handler receives the cpu load from an instance and updates the
      * nodeStatistics in LoadBalancerAlgorithm
      */
     Handler<MyCPULoadAndBandwidth> myCPULoadHandler = new Handler<MyCPULoadAndBandwidth>() {
          @Override
          public void handle(MyCPULoadAndBandwidth event) {
               loadBalancerAlgorithm.updateCPULoadFor(event.node(), event.cpuLoad());
          cpuLoads.put(event.getSource(), event.cpuLoad());
          bandwidths.put(event.getSource(), event.currentBandwidth());
          CloudGUI.getInstance().updateCPULoadForNode(event.node(), event.cpuLoad());
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
			if (event.trainingData()) {
				TrainingData data = new TrainingData(self, event.controller());
                data.setNrNodes(event.numberOfNodes());
				data.setResponseTimeMean(event.getAverageResponseTime());
				data.setThroughputMean(event.getAverageThroughput());
                data.setBandwidthMean(calculateBandwidthMean());
				data.setPeriodicTotalCost(event.getPeriodicTotalCost());
                data.setTotalCost(event.getTotalCost());
                setCpuLoadAndSTD(data);
                sla.addBandwidth(data.getBandwidthMean());
                setSLAViolations(data, sla, event);
                trigger(data, network);
			} else {
				SenseData data = new SenseData(self, event.controller());
                data.setNrNodes(event.numberOfNodes());
				data.setResponseTimeMean(event.getAverageResponseTime());
                data.setThroughputMean(event.getAverageThroughput());
                data.setBandwidthMean(calculateBandwidthMean());
                data.setPeriodicTotalCost(event.getPeriodicTotalCost());
                data.setTotalCost(event.getTotalCost());
                setCpuLoadAndSTD(data);
                sla.addBandwidth(data.getBandwidthMean());
                setSLAViolations(data, sla, event);
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

    /**
     * This handler is triggered when the cloudAPI ask for some nodes so he can remove. ELB selects
     * the nodes with the least CPU load according to ELB algorithm that is running.
     */
    Handler<SelectNodesToRemove> selectNodesToRemoveHandler = new Handler<SelectNodesToRemove>() {
        @Override
        public void handle(SelectNodesToRemove event) {
            List<Node> nodes = loadBalancerAlgorithm.selectNodesToRemove(event.nodes(), event.numberOfNodesToRemove());
            trigger(new NodesToRemove(nodes), elb);
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
        bandwidths.clear();
		return average;
	}

	protected void scheduleUpdateELBTree() {
		ScheduleTimeout st = new ScheduleTimeout(ELB_TREE_UPDATE_INTERVAL);
		st.setTimeoutEvent(new UpdateELBTree(st));
		trigger(st, timer);
	}

    private void setCpuLoadAndSTD(StateVariables st) {
        if (cpuLoads.size() == 0) {
            st.setCpuLoadMean(0.0);
            st.setCpuLoadSTD(0.0);
            return;
        }
        double sum = 0.0;
        double average = 0.0;
        synchronized (cpuLoads) {
            for (Address node: cpuLoads.keySet())
                sum += cpuLoads.get(node);
            average = sum/cpuLoads.size();
            st.setCpuLoadMean(average);
            sla.addCpuLoad(average);
            sum = 0;
            for (Address node : cpuLoads.keySet())
                sum += Math.pow(cpuLoads.get(node)-average, 2.0);
            st.setCpuLoadSTD(Math.sqrt(sum/cpuLoads.size()));
        }
        cpuLoads.clear();
    }

	protected void startELBTable(ELBInit event) {
		elbTable = new ELBTable(event.replicationDegree());
		for (Block block : event.blocks()) {
			ELBEntry entry = new ELBEntry(block.getName(), block.getSize());
			elbTable.addEntry(entry);
		}
	}

    private void getSLA(ELBInit event) {
        if (event.sla() != null) {
            slaEnabled = true;
            sla = event.sla();
            logger.info("SLA requirements: " + sla);
        }
    }

    private void setSLAViolations(SLAViolation obj, SLA sla, SendRawData event) {
        event.updateSLA(sla);
        obj.setCpuLoadViolation(sla.getCpuLoadViolation());
        obj.setResponseTimeViolation(sla.getResponseTimeViolation());
        obj.setBandwidthViolation(sla.getBandwidthViolation());
    }


}
