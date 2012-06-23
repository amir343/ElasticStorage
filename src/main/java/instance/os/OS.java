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
package instance.os;

import cloud.api.RestartInstance;
import cloud.common.Alive;
import cloud.common.HeartbeatMessage;
import cloud.common.NodeConfiguration;
import cloud.common.RequestMessage;
import cloud.elb.ActivateBlock;
import cloud.elb.MyCPULoadAndBandwidth;
import cloud.requestengine.DownloadStarted;
import econtroller.sensor.Monitor;
import instance.Node;
import instance.common.*;
import instance.cpu.CPU;
import instance.cpu.OperationDuration;
import instance.disk.StartDiskUnit;
import instance.gui.DummyInstanceGUI;
import instance.gui.GenericInstanceGUI;
import instance.gui.HeadLessGUI;
import instance.gui.InstanceGUI;
import instance.mem.StartMemoryUnit;
import logger.Logger;
import logger.LoggerFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 */

public class OS extends ComponentDefinition {
	
	private Logger logger;
	
	// Ports
	Positive<CPUChannel> cpu = requires(CPUChannel.class);
	Positive<MemChannel> memory = requires(MemChannel.class);
	Positive<DiskChannel> disk = requires(DiskChannel.class);
	Positive<Timer> timer = requires(Timer.class);
	Positive<Network> network = requires(Network.class);
	Negative<OSPort> os = provides(OSPort.class);
	
	// Variables needed by OS
	private String uname_r = "2.2-2";
	private Kernel kernel;
	protected final int numberOfDevices = 3;
	private int numberOfDevicesLoaded = 0;
	private long BANDWIDTH = 2 * Size.MB.getSize();
	private static final long WAIT = 1000;
	private static final long REQUEST_QUEUE_PROCESSING_INTERVAL = 1000;
	private static final long CPU_LOAD_PROPAGATION_INTERVAL = 5000;
	public static final long RESTART_PERIOD = 60000;
	private static final long COST_CALCULATION_INTERVAL = 10000;
	private static int simultaneousDownloads = 70;
	protected Address cloudProvider;
	private ConcurrentMap<String, Process> pt = new ConcurrentHashMap<String, Process>();
	private ConcurrentMap<UUID, String> currentTransfers = new ConcurrentHashMap<UUID, String>();
	private final ConcurrentLinkedQueue<Request> requestQueue = new ConcurrentLinkedQueue<Request>();
	private CostService costService = new CostService();
	protected GenericInstanceGUI gui;
	protected Address self;
	protected Node node;
	protected NodeConfiguration nodeConfiguration;
    protected double currentCpuLoad;
    private XYSeriesCollection dataSet = new XYSeriesCollection();
    private XYSeries xySeries = new XYSeries("Load");
    private long startTime = System.currentTimeMillis();
    private int lastSnapshotID = 1;
    private long currentBandwidth = BANDWIDTH;
    private List<Block> blocks = new ArrayList<Block>();
/*
    protected boolean acceptRequest = true;
*/
    private boolean enabled = true;
    private int megaBytesDownloadedSoFar = 0;
    protected double totalCost = 0.0;
    private boolean headless;
    private OS selfReference;
    private Map<String, Address> dataBlocks;

    public OS() {
        this.selfReference = this;

		subscribe(initHandler, control);

		subscribe(cpuReadySignalHandler, cpu);
		subscribe(cpuLoadHandler, cpu);
		subscribe(snapshotRequestHandler, cpu);

		subscribe(memoryReadySignalHandler, memory);
		subscribe(ackBlockHandler, memory);
		subscribe(nackBlockHandler, memory);

		subscribe(diskReadySignalHandler, disk);
		subscribe(blockResponseHandler, disk);

		subscribe(processRequestQueueHandler, timer);
		subscribe(propagateCPULoadHandler, timer);
		subscribe(transferringFinishedHandler, timer);
		subscribe(readDiskFinishedHandler, timer);
		subscribe(waitTimeoutHandler, timer);
		subscribe(deathHandler, timer);
		subscribe(calculateCostHandler, timer);

		subscribe(requestMessageHandler, network);
		subscribe(heartbeatMessageHandler, network);
		subscribe(monitorHandler, network);
		subscribe(shutDownHandler, network);
		subscribe(restartInstanceHandler, network);
		subscribe(rebalanceRequestHandler, network);
		subscribe(rebalanceResponseHandler, network);
		subscribe(blockTransferredHandler, network);
        subscribe(closeMyStreamHandler, network);
	}

    // DONE
    Handler<OSInit> initHandler = new Handler<OSInit>() {
		@Override
		public void handle(OSInit event) {
			retrieveInitParameters(event);
			loadKernel();
			loadBlocksToDisk(event);
			dataSet.addSeries(xySeries);
			waitForSystemStartUp();
			costService.init(event.getNodeConfiguration());
			gui.updateBandwidthInfoLabel(Size.getSizeString(BANDWIDTH));
		}

        // DONE
		private void retrieveInitParameters(OSInit event) {
            nodeConfiguration = event.getNodeConfiguration();
			BANDWIDTH = nodeConfiguration.getBandwidthConfiguration().getBandwidthMegaBytePerSecond();
			simultaneousDownloads = nodeConfiguration.getSimultaneousDownloads();
			self = event.getSelfAddress();
			node = event.getNodeInfo();
			cloudProvider = event.getCloudProviderAddress();
            headless = nodeConfiguration.getHeadLess();
            if (headless) {
                gui = new HeadLessGUI();
                logger = LoggerFactory.getLogger(OS.class, new DummyInstanceGUI());
                kernel = new Kernel(true);
            } else {
                gui = InstanceGUI.getInstance();
		        gui.setOSReference(selfReference);
                logger = LoggerFactory.getLogger(OS.class, InstanceGUI.getInstance());
                kernel = new Kernel(false);
            }
            logger.info("NodeConfigurations:\n" + nodeConfiguration.toString());
            gui.updateSimultaneousDownloads(String.valueOf(simultaneousDownloads));

		}
	};

	/**
	 * This handler is in charge of handling the request that are sent by Elastic Load Balancer
	 */
    // DONE
	Handler<RequestMessage> requestMessageHandler = new Handler<RequestMessage>() {
		@Override
		public void handle(RequestMessage event) {
			if (instanceRunning()) {
				Request req = event.request();
                logger.debug("Received request for block " + req);
                if (simultaneousDownloads > currentTransfers.size()) {
                    logger.debug("Admitted Request for block '" + req.getBlockId() + "'");
                    requestQueue.add(req);
                }
                else {
                    logger.warn("Rejected Request for download block " + req.getBlockId() + ". No free slot. {simDown: " + simultaneousDownloads + ", currenTrans: " + currentTransfers.size() + "}");
                    Rejected rejected = new Rejected(self, cloudProvider, event.request());
                    trigger(rejected, network);
                }
			}
		}
	};

	/**
	 * This event handler is triggered periodically to process request queue
	 */
    // DONE
	Handler<ProcessRequestQueue> processRequestQueueHandler = new Handler<ProcessRequestQueue>() {
		@Override
		public void handle(ProcessRequestQueue event) {
			if (instanceRunning()) {
				int freeSlot = simultaneousDownloads - currentTransfers.size();
				trigger(new MemoryCheckOperation(), cpu);
				for (int i=0; i<freeSlot; i++) {
					if (!requestQueue.isEmpty()) {
						Request req = requestQueue.remove();
						trigger(new MemoryCheckOperation(), cpu);
						trigger(new MemoryReadOperation(8), cpu);
						startProcessForRequest(req);
					} else break;
				}
                gui.updateRequestQueue(requestQueue.size());
                synchronized (requestQueue) {
                    for (int i=0; i < requestQueue.size(); i++) {
                        Request req = requestQueue.remove();
                        if (req != null) trigger(new Rejected(self, cloudProvider, req), network);
                    }
                }
			}
			scheduleProcessingRequestQueue();
		}
	};

    // DONE
	Handler<Ready> cpuReadySignalHandler = new Handler<Ready>() {
		@Override
		public void handle(Ready event) {
			numberOfDevicesLoaded++;
			for (int i=0; i<10; i++)
				trigger(new BootOperation(), cpu);
			logger.debug("Received ready signal from " + event.getDevice());
			if (instanceRunning()) osStarted();
		}
	};

    // DONE
	Handler<Ready> memoryReadySignalHandler = new Handler<Ready>() {
		@Override
		public void handle(Ready event) {
			numberOfDevicesLoaded++;
			logger.debug("Received ready signal from " + event.getDevice());
			if (instanceRunning()) osStarted();
		}
	};

    // DONE
	Handler<Ready> diskReadySignalHandler = new Handler<Ready>() {
		@Override
		public void handle(Ready event) {
			numberOfDevicesLoaded++;
			logger.debug("Received ready signal from " + event.getDevice());
			if (instanceRunning()) osStarted();
		}
	};

	/**
	 * Memory hit: this means the next operation is to transfer the block
	 * from memory into the network according to the bandwidth we have
	 */
    // DONE
	Handler<AckBlock> ackBlockHandler = new Handler<AckBlock>() {
		@Override
		public void handle(AckBlock event) {
			if (instanceRunning()) {
				logger.debug("Block " + event.getProcess().getRequest().getBlockId() + " exists in the memory");
				scheduleTransferForBlock(event.getProcess());
				trigger(new MemoryReadOperation(event.getProcess().getBlockSize()), cpu);
			}
		}
	};

	/**
	 * Memory miss: this means that the next operation is an I/O from the disk
	 * that may take much more time than a memory read
	 */
    // DONE
	Handler<NAckBlock> nackBlockHandler = new Handler<NAckBlock>() {
		@Override
		public void handle(NAckBlock event) {
			if (instanceRunning()) {
				logger.debug("Block " + event.getProcess().getRequest().getBlockId() + " does not exists in the memory");
				ReadBlock read = new ReadBlock(event.getProcess().getRequest().getBlockId());
				read.setProcess(event.getProcess());
				trigger(read, disk);
			}
		}
	};

	/**
	 * This is the response from Disk containing the block requested earlier
	 */
    // DONE
	Handler<BlockResponse> blockResponseHandler = new Handler<BlockResponse>() {
		@Override
		public void handle(BlockResponse event) {
			if (instanceRunning()) {
				readFromDiskIntoMemory(event);
				WriteBlockIntoMemory write = new WriteBlockIntoMemory(event.getBlock());
				trigger(new DiskReadOperation(event.getBlock().getSize()), cpu);
				trigger(new MemoryWriteOperation(event.getBlock().getSize()), cpu);
				trigger(write, memory);
			}
		}
	};

	/**
	 * Updates transfer table and compute bandwidth for remaining transfers
	 */
    // DONE
	Handler<TransferringFinished> transferringFinishedHandler = new Handler<TransferringFinished>() {
		@Override
		public void handle(TransferringFinished event) {
			if (instanceRunning()) {
				Process process = pt.get(event.getPid());
/*
                checkIfCanAcceptRequest();
*/
                if (process != null) {
                    Request request = process.getRequest();
                    updateTransferredBandwidth(process);
                    currentTransfers.remove(event.getTimeoutId());
                    gui.decreaseNrDownloadersFor(request.getBlockId());
                    gui.updateCurrentTransfers(currentTransfers.size());
                    informDownloader(event, process, request);
                    removeFromProcessTable(event.getPid());
                    endProcessOnCPU(event.getPid());
                    if (currentTransfers.size() != 0) {
                        cancelAllPreviousTimers();
                        rescheduleAllTimers(BANDWIDTH/currentTransfers.size(), System.currentTimeMillis());
                    }
                }
			}
		}

        // DONE
		private synchronized void updateTransferredBandwidth(Process process) {
			megaBytesDownloadedSoFar += (process.getBlockSize()/(1024*1024));
		}

        // DONE
		private void informDownloader(TransferringFinished event, Process process, Request request) {
			if (request.getDestinationNode() != null) {
				logger.info("Rebalancing finished for " + event.getPid());
				trigger(new BlockTransferred(self, request.getDestinationNode(), process.getBlockSize(), process.getRequest().getBlockId()), network);
			} else {
				logger.debug("Transferring finished for " + event.getPid() );
			}
		}

    };

	/**
	 * This means that the block is read from the disk into memory. Now we can transfer
	 * it into the network
	 */
    // DONE
	Handler<ReadDiskFinished> readDiskFinishedHandler = new Handler<ReadDiskFinished>() {
		@Override
		public void handle(ReadDiskFinished event) {
			if (instanceRunning()) {
				scheduleTransferForBlock(pt.get(event.getPid()));
				trigger(new MemoryCheckOperation(), cpu);
			}
		}
	};

	/**
	 * This handler is executed if the system has not yet started until it starts successfully with all the hardware components
	 */
    // DONE
	Handler<WaitTimeout> waitTimeoutHandler = new Handler<WaitTimeout>() {
		@Override
		public void handle(WaitTimeout event) {
			if (!instanceRunning()) {
				waitForSystemStartUp();
			} else {
				scheduleProcessingRequestQueue();
				scheduleCPULoadPropagationToCloudProvider();
				scheduleCostCalculation();
			}
		}
	};

	/**
	 * This handler is triggered upon receiving a shutDown request from cloudProvider
	 */
    // DONE
	Handler<ShutDown> shutDownHandler = new Handler<ShutDown>() {
		@Override
		public void handle(ShutDown event) {
			if (event.getSource().equals(cloudProvider)) {
				logger.debug("Time to die forever :(");
				if (!headless) {
                    ((InstanceGUI)gui).dispose();
                }
                for (Map.Entry<String, Address> entry : dataBlocks.entrySet()) {
                    trigger(new CloseMyStream(self, entry.getValue()), network);
                }
				trigger(new ShutDownAck(self, event.getSource(), node), network);
				scheduleDeath();
			} else {
				logger.warn("Oh oh! Someone is trying to kill me!");
			}
		}
	};

    /**
     * This handler is triggered by dying instance to clean up open streams that can block accepting further requests
     */
    // DONE
    Handler<CloseMyStream> closeMyStreamHandler = new Handler<CloseMyStream>(){
        @Override
        public void handle(CloseMyStream event) {
            removeProcessesBelongTo(event);
/*
            checkIfCanAcceptRequest();
*/
        }
    };

    // DONE
    private synchronized void removeProcessesBelongTo(CloseMyStream event) {
        for (String processId : pt.keySet()) {
            if (pt.get(processId).getRequest().getDestinationNode() != null &&
                    pt.get(processId).getRequest().getDestinationNode().equals(event.getSource()))
                pt.remove(processId);
        }
    }

    /**
	 * This is for scheduling a complete shut down if the CloudProvider
	 * was not able to shut down this instance completely (because of port binding)
	 */
    // DONE
	Handler<Death> deathHandler = new Handler<Death>() {
		@Override
		public void handle(Death event) {
			System.exit(0);
		}
	};

	/**
	 * This handler is triggered when receives a heatBeat message from HealthChecker
	 */
    // DONE
	Handler<HeartbeatMessage> heartbeatMessageHandler = new Handler<HeartbeatMessage>() {
		@Override
		public void handle(HeartbeatMessage event) {
			if (instanceRunning()) {
				trigger(new Alive(self, event.getSource()), network);
			}
		}
	};

	/**
	 * This handler is triggered by CPU
	 */
    // DONE
	Handler<CPULoad> cpuLoadHandler = new Handler<CPULoad>() {
		@Override
		public void handle(CPULoad event) {
			if (instanceRunning()) {
				currentCpuLoad = event.getCpuLoad();
			}
		}
	};

	/**
	 * Upon every tick of CPU_LOAD_PROPAGATION_INTERVAL, this handler sends
	 * the current cpu load to cloud provider
	 */
    // DONE
	Handler<PropagateCPULoad> propagateCPULoadHandler = new Handler<PropagateCPULoad>() {
		@Override
		public void handle(PropagateCPULoad event) {
			if (instanceRunning()) {
				trigger(new MyCPULoadAndBandwidth(self, cloudProvider, node, currentCpuLoad, currentBandwidth), network);
				trigger(new MemoryCheckOperation(), cpu);
				scheduleCPULoadPropagationToCloudProvider();
			}
		}
	};

	/**
	 * This handler is for getting CPU Load chart from CPU
	 */
    // DONE
	Handler<SnapshotRequest> snapshotRequestHandler = new Handler<SnapshotRequest>() {
		@Override
		public void handle(SnapshotRequest event) {
			if (instanceRunning()) {
				InstanceSnapshot snapshot = new InstanceSnapshot(lastSnapshotID++);
				snapshot.addCPULoadChart(event.getCpuLoadChart());
				snapshot.addBandwidthChart(getBandwidthChart());
				gui.addSnapshot(snapshot);
			}
		}
	};

	/**
	 * This handler is triggered by Sensor component
	 */
    @Deprecated
	Handler<Monitor> monitorHandler = new Handler<Monitor>() {
		@Override
		public void handle(Monitor event) {
			if (instanceRunning()) {
				logger.info("Recieved Monitor from Sensor");
			}
		}
	};

	/**
	 * This handler is triggered by Cloud Provider
	 */
    // DONE
	Handler<RestartInstance> restartInstanceHandler = new Handler<RestartInstance>() {
		@Override
		public void handle(RestartInstance event) {
			if (instanceRunning()) {
				restartInstance();
			}
		}
	};

    /**
	 * This handler sends a request to another instance asking for the recommended blocks that it received 
	 * from cloud provider.
	 */
	Handler<RebalanceRequest> rebalanceRequestHandler = new Handler<RebalanceRequest>() {
		@Override
		public void handle(RebalanceRequest event) {
			Block block = findRequestedBlock(event.getBlockId());
			trigger(new RebalanceResponse(self, event.getSource(), block), network);
/*
			acceptRequest = false;
*/
			Request req = new Request(UUID.randomUUID().toString(), block.getName(), event.getSource());
			startProcessForRequest(req);
		}
	};
	
	/**
	 * This handler is a response from the other instance with the data blocks 
	 */
	Handler<RebalanceResponse> rebalanceResponseHandler = new Handler<RebalanceResponse>() {
		@Override
		public void handle(RebalanceResponse event) {
			blocks.add(event.getBlock());
			logger.debug("Rebalancing is started from " + event.getSource() + " for block " + event.getBlock().getName());
			while(!instanceRunning()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
			Process p = Process.createAbstractProcess();
			addToProcessTable(p);
			startTransferProcessOnCPU(p);

			currentBandwidth = BANDWIDTH / blocks.size();
			addToBandwidthDiagram(currentBandwidth);
		}
	};
	
	/**
	 * This handler is triggered when the transfer from another instance node is finished
	 */
    // DONE
	Handler<BlockTransferred> blockTransferredHandler = new Handler<BlockTransferred>() {
		@Override
		public void handle(BlockTransferred event) {
			String process = pt.keySet().iterator().next();
			pt.remove(process);
			Block block = new Block (event.getBlockName(), event.getBlockSize());
			ActivateBlock activateBlock = new ActivateBlock(self, cloudProvider, node, block);
			trigger(activateBlock, network);
			endProcessOnCPU(process);
			trigger(new DiskWriteOperation(event.getBlockSize()), cpu);
			calculateNewBandwidth();
			addToBandwidthDiagram(currentBandwidth);
            if (blocks.size() == dataBlocks.size()) {
                logger.info("Starting with " + blocks.size() + " block(s) in hand");
                LoadBlock load = new LoadBlock(blocks);
                trigger(load, disk);
                gui.initializeDataBlocks(blocks);
            }
        }

        // DONE
		private void calculateNewBandwidth() {
			if (pt.size() == 0 ) {
				currentBandwidth = BANDWIDTH;
			} else {
				currentBandwidth = BANDWIDTH / pt.size();
			}
		}
	};

	/**
	 * This handler is triggered to calculate the current cost for this instance from its start up time
	 */
    // DONE
	Handler<CalculateCost> calculateCostHandler = new Handler<CalculateCost>() {
		@Override
		public void handle(CalculateCost event) {
			totalCost = costService.computeCostSoFar(megaBytesDownloadedSoFar);
			double costToSend = costService.computeCostInThisPeriod(megaBytesDownloadedSoFar);
			DecimalFormat df = new DecimalFormat("##.####");
			String totalCostString = df.format(totalCost);
			String periodicCostString = df.format(costToSend);
			gui.updateCurrentCost(totalCostString);
			trigger(new InstanceCost(self, cloudProvider, node, totalCostString, periodicCostString), network);
			scheduleCostCalculation();
		}
	};

    // DONE
	private void osStarted() {
		logger.debug("OS with kernel " + uname_r + " started");
		gui.decorateSystemStarted();
	}

    // DONE
	protected void scheduleCostCalculation() {
		ScheduleTimeout st = new ScheduleTimeout(COST_CALCULATION_INTERVAL);
		st.setTimeoutEvent(new CalculateCost(st));
		trigger(st, timer);		
	}

    // DONE
	protected Block findRequestedBlock(String blockId) {
		for (Block block : blocks) {
			if (block.getName().equals(blockId))
				return block;
		}
		return null;
	}

    // DONE
	protected void scheduleCPULoadPropagationToCloudProvider() {
		ScheduleTimeout st = new ScheduleTimeout(CPU_LOAD_PROPAGATION_INTERVAL);
		st.setTimeoutEvent(new PropagateCPULoad(st));
		trigger(st, timer);
		gui.createBandwidthDiagram(getBandwidthChart());
	}

    // DONE
	protected void scheduleProcessingRequestQueue() {
		ScheduleTimeout st = new ScheduleTimeout(REQUEST_QUEUE_PROCESSING_INTERVAL);
		st.setTimeoutEvent(new ProcessRequestQueue(st));
		trigger(st, timer);		
	}

    // DONE
	protected void loadKernel() {
		gui.updateTitle(node.getNodeName() + "@" + node.getIP() + ":" + node.getPort());
		gui.decorateWhileSystemStartUp();
		enabled = false;
		kernel.init(nodeConfiguration.getCpuConfiguration().getCpuSpeedInstructionPerSecond());
		enabled = true;
	}

    // DONE
	protected void scheduleDeath() {
		ScheduleTimeout st = new ScheduleTimeout(1000);
		st.setTimeoutEvent(new Death(st));
		trigger(st, timer);		
	}

    // DONE
	private void waitForSystemStartUp() {
		ScheduleTimeout st = new ScheduleTimeout(WAIT);
		st.setTimeoutEvent(new WaitTimeout(st));
		trigger(st, timer);		
	}

    // DONE
	private void readFromDiskIntoMemory(BlockResponse response) {
		Process process = response.getProcess();
		Block block = response.getBlock();
		process.setBlockSize(block.getSize());
		ScheduleTimeout st = new ScheduleTimeout(OperationDuration.getDiskReadDuration(CPU.CPU_CLOCK, response.getBlock().getSize()));
		ReadDiskFinished rd = new ReadDiskFinished(st);
		rd.setPid(process.getPid());
		st.setTimeoutEvent(rd);
		trigger(st, timer);		
	}

    // DONE
	private synchronized void scheduleTransferForBlock(Process process) {
		if (currentTransfers.size() == 0) {
			logger.debug("Transfer started " + process);
			scheduleTimeoutFor(process, BANDWIDTH);
			addToBandwidthDiagram(BANDWIDTH);
			currentBandwidth = BANDWIDTH;
		}  else {
			trigger(new MemoryCheckOperation(), cpu);
			long newBandwidth = BANDWIDTH / (currentTransfers.size() + 1 );
			long now = System.currentTimeMillis();
			cancelAllPreviousTimers();
			rescheduleAllTimers(newBandwidth, now);
			logger.debug("Transfer started " + process);
			scheduleTimeoutFor(process, newBandwidth);
		}
		trigger(new DownloadStarted(self, cloudProvider, process.getRequest().getId()), network);
	}

    // DONE
	private void rescheduleAllTimers(long newBandwidth, long now) {
		logger.debug("Rescheduling all current downloads with bandwidth: " + newBandwidth + " B/s");
		addToBandwidthDiagram(newBandwidth);	
		currentBandwidth = newBandwidth;
		ConcurrentMap<UUID, String> ct = new ConcurrentHashMap<UUID, String>();
		
		for (Entry<UUID, String> en : currentTransfers.entrySet()) {
			trigger(new MemoryCheckOperation(), cpu);
			Process p = pt.get(en.getValue());
			if (p == null ) continue;
			p.setRemainingBlockSize( p.getRemainingBlockSize() - (long)(now - p.getSnapshot()) * p.getCurrentBandwidth()/1000);
			if (p.getRemainingBlockSize() < 0) p.setRemainingBlockSize(0);
			p.setCurrentBandwidth(newBandwidth);
			p.setTimeout(p.getRemainingBlockSize() / p.getCurrentBandwidth());
			p.setSnapshot(now);
			pt.put(p.getPid(), p);
			ScheduleTimeout st = new ScheduleTimeout(1000 * p.getRemainingBlockSize() / p.getCurrentBandwidth());
			TransferringFinished tt = new TransferringFinished(st);
			tt.setPid(p.getPid());
			st.setTimeoutEvent(tt);
			ct.put(st.getTimeoutEvent().getTimeoutId(), p.getPid());
			trigger(st, timer);
		}
		
		currentTransfers.clear();
		currentTransfers.putAll(ct);
	}

	// DONE
    private void scheduleTimeoutFor(Process process, long bandwidth) {
		trigger(new MemoryCheckOperation(), cpu);
		long transferDelay = 1000 * process.getBlockSize() / bandwidth ;
		ScheduleTimeout st = new ScheduleTimeout(1000 * process.getBlockSize() / bandwidth );
		TransferringFinished tt = new TransferringFinished(st);
		tt.setPid(process.getPid());
		st.setTimeoutEvent(tt);
		UUID timeoutId = st.getTimeoutEvent().getTimeoutId();
		currentTransfers.put(timeoutId, process.getPid());
		process.setCurrentBandwidth(bandwidth).setRemainingBlockSize(process.getBlockSize()).setSnapshot(System.currentTimeMillis()).setTimeout(transferDelay);
		pt.put(process.getPid(), process);
		trigger(st, timer);
	}

    // DONE
	private void cancelAllPreviousTimers() {
		for (Entry<UUID, String> en : currentTransfers.entrySet()) {
			trigger(new MemoryCheckOperation(), cpu);
			CancelTimeout cancel = new CancelTimeout(en.getKey());
			trigger(cancel, timer);
		}
	}

    // DONE
	protected void startProcessForRequest(Request event) {
		Process p = new Process(event);
		startTransferProcessOnCPU(p);
		addToProcessTable(p);
		checkMemory(p);		
		gui.increaseNrDownloadersFor(event.getBlockId());
        gui.updateCurrentTransfers(currentTransfers.size());
	}

    // DONE
	private void checkMemory(Process p) {
		RequestBlock rBlock = new RequestBlock(p);
		trigger(rBlock, memory);
		trigger(new MemoryCheckOperation(), cpu);
	}

    // DONE
	private void addToProcessTable(Process p) {
		pt.put(p.getPid(), p);		
	}
	
	private void removeFromProcessTable(String pid) {
		pt.remove(pid);		
	}

    // DONE
	private void startTransferProcessOnCPU(Process p) {
		StartProcess process = new StartProcess(p);
		trigger(process, cpu);		
	}
	
	private void endProcessOnCPU(String pid) {
		EndProcess process = new EndProcess(pid);
		trigger(process, cpu);		
	}

    // DONE
	protected void loadBlocksToDisk(OSInit event) {
		if (event.getBlocks() != null ) {
			blocks = event.getBlocks();
			logger.info("Starting with " + blocks.size() + " block(s) in hand");
			LoadBlock load = new LoadBlock(blocks);
			trigger(load, disk);
			trigger(new BlocksAck(self, cloudProvider, node), network);
			gui.initializeDataBlocks(blocks);
		} else {
			logger.warn("I should get blocks from " + event.getNodeConfiguration().getDataBlocksMap().size() + " other instance(s)");
			dataBlocks = event.getNodeConfiguration().getDataBlocksMap();
			for (String blockId : dataBlocks.keySet()) {
				trigger(new RebalanceRequest(self, dataBlocks.get(blockId), blockId), network);
			}
			addToBandwidthDiagram(BANDWIDTH);
		}
		trigger(new InstanceStarted(self, cloudProvider, node), network);
	}

    // DONE
	private void addToBandwidthDiagram(long bandWidth) {
		xySeries.add(System.currentTimeMillis() - startTime, bandWidth);		
	}

	private JFreeChart getBandwidthChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Bandwidth per download", "Time (ms)", "Bandwidth (B/s)", dataSet, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = chart.getXYPlot();
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		return chart;
	}

	public void takeSnapshot() {
		trigger(new SnapshotRequest(), cpu);		
	}

	private boolean instanceRunning() {
        return numberOfDevices == numberOfDevicesLoaded && enabled;
    }

    // DONE
    public void restartInstance() {
        trigger(new RestartSignal(), cpu);
        trigger(new RestartSignal(), memory);
        trigger(new RestartSignal(), disk);
        gui.systemRestart();

        logger.warn("System restarting...");
        numberOfDevicesLoaded = 0;
        pt.clear();
        cancelAllPreviousTimers();
        currentTransfers.clear();
        requestQueue.clear();
        xySeries.clear();
        scheduleCPULoadPropagationToCloudProvider();

        kernel.shutdown();
        gui.decorateWhileSystemStartUp();

        trigger(new StartMemoryUnit(), memory);
        trigger(new StartDiskUnit(), disk);
        loadKernel();
        scheduleCPULoadPropagationToCloudProvider();
        scheduleProcessingRequestQueue();

        gui.decorateSystemStarted();
    }

/*
    private synchronized void checkIfCanAcceptRequest() {
        if (!acceptRequest) {
            boolean found = false;
            for (String pid : pt.keySet()) {
                if (pt.get(pid).getRequest().getDestinationNode() != null) {
                    found = true;
                    break;
                }
            }
            if (!found)
                acceptRequest = true;
        }
        trigger(new MemoryCheckOperation(), cpu);
    }
*/

}
