package instance.os;

import instance.Node;
import instance.common.AckBlock;
import instance.common.Block;
import instance.common.BlockResponse;
import instance.common.BlocksAck;
import instance.common.BootOperation;
import instance.common.CPUChannel;
import instance.common.Death;
import instance.common.DiskChannel;
import instance.common.DiskReadOperation;
import instance.common.DiskWriteOperation;
import instance.common.EndProcess;
import instance.common.InstanceStarted;
import instance.common.LoadBlock;
import instance.common.MemChannel;
import instance.common.MemoryCheckOperation;
import instance.common.MemoryReadOperation;
import instance.common.MemoryWriteOperation;
import instance.common.NAckBlock;
import instance.common.OSInit;
import instance.common.OSPort;
import instance.common.ReadBlock;
import instance.common.ReadDiskFinished;
import instance.common.Ready;
import instance.common.Request;
import instance.common.RequestBlock;
import instance.common.ShutDown;
import instance.common.ShutDownAck;
import instance.common.Size;
import instance.common.StartProcess;
import instance.common.TransferingFinished;
import instance.common.WaitTimeout;
import instance.common.WriteBlockIntoMemory;
import instance.cpu.CPU;
import instance.cpu.OperationDuration;
import instance.disk.StartDiskUnit;
import instance.gui.InstanceGUI;
import instance.mem.StartMemoryUnit;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

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
import cloud.api.RestartInstance;
import cloud.common.Alive;
import cloud.common.HeartbeatMessage;
import cloud.common.NodeConfiguration;
import cloud.common.RequestMessage;
import cloud.elb.MyCPULoad;
import cloud.requestengine.RequestDone;
import econtroller.sensor.Monitor;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 */

public class OS extends ComponentDefinition {
	
	private Logger logger = LoggerFactory.getLogger(OS.class, InstanceGUI.getInstance());
	
	// Ports
	Positive<CPUChannel> cpu = requires(CPUChannel.class);
	Positive<MemChannel> memory = requires(MemChannel.class);
	Positive<DiskChannel> disk = requires(DiskChannel.class);
	Positive<Timer> timer = requires(Timer.class);
	Positive<Network> network = requires(Network.class);
	Negative<OSPort> os = provides(OSPort.class);
	
	// Variables needed by OS
	private String uname_r = "2.2-2";
	private Kernel kernel = new Kernel();
	protected final int numberOfDevices = 3;
	private int numberOfDevicesLoaded = 0;
	private long BANDWIDTH = 2 * Size.MB.getSize();
	private static final long WAIT = 1000;
	private static final long REQUEST_QUEUE_PROCESSING_INTERVAL = 1000;
	private static final long CPU_LOAD_PROPAGATION_INTERVAL = 5000;
	public static final long RESTART_PERIOD = 60000;
	private int simultaneousDownloads = 20;
	protected Address cloudProvider;
	private ConcurrentMap<String, Process> pt = new ConcurrentHashMap<String, Process>();
	private ConcurrentMap<UUID, String> currentTransfers = new ConcurrentHashMap<UUID, String>();
	private ConcurrentLinkedQueue<Request> requestQueue = new ConcurrentLinkedQueue<Request>();
	protected InstanceGUI gui;
	protected Address self;
	protected Node node;
	protected NodeConfiguration nodeConfiguration;
	protected double currentCpuLoad;
	private XYSeriesCollection dataSet = new XYSeriesCollection();
	private XYSeries xySeries = new XYSeries("Load");
	private long startTime = System.currentTimeMillis();
	private int lastSnapshotID = 1;
	private long currentBandwidth;
	private List<Block> blocks = new ArrayList<Block>();
	protected boolean acceptRequest = true;
	private boolean enabled = true;	
	
	public OS() {
		gui = InstanceGUI.getInstance();
		gui.setOSReference(this);

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
		subscribe(transferingFinishedHandler, timer);
		subscribe(readDiskFinishedHandler, timer);
		subscribe(waitTimeoutHandler, timer);
		subscribe(deathHandler, timer);
		subscribe(OSRestartTimeoutHandler, timer);
		
		subscribe(requestMessageHandler, network);
		subscribe(heartbeatMessageHandler, network);
		subscribe(monitorHandler, network);
		subscribe(shutDownHandler, network);
		subscribe(restartInstanceHandler, network);
		subscribe(rebalanceRequestHandler, network);
		subscribe(rebalanceResponseHandler, network);
		subscribe(blockTransferedHandler, network);
	}
	
	Handler<OSInit> initHandler = new Handler<OSInit>() {
		@Override
		public void handle(OSInit event) {
			retrieveInitParameters(event);
			loadKernel();
			loadBlocksToDisk(event);
			dataSet.addSeries(xySeries);
			waitForSystemStartUp();
			gui.updateBandwidthInfoLabel(Size.getSizeString(BANDWIDTH));
		}

		private void retrieveInitParameters(OSInit event) {
			nodeConfiguration = event.getNodeConfiguration();
			BANDWIDTH = event.getNodeConfiguration().getBandwidthMegaBytePerSecond();
			simultaneousDownloads = event.getNodeConfiguration().getSimultaneousDownloads();
			self = event.getSelfAddress();
			node = event.getNodeInfo();
			cloudProvider = event.getCloudProviderAddress();
		}
	};
	
	/**
	 * This handler is simple time out event indicating that the OS is restarted
	 */
	Handler<OSRestartTimeout> OSRestartTimeoutHandler = new Handler<OSRestartTimeout>() {
		@Override
		public void handle(OSRestartTimeout event) {
			trigger(new StartMemoryUnit(), memory);
			trigger(new StartDiskUnit(), disk);
			loadKernel();
			scheduleCPULoadPropagationToCloudProvider();
			scheduleProcessingRequestQueue();
		}
	};

	/**
	 * This handler is in charge of handling the request that are sent by Elastic Load Balancer
	 */
	Handler<RequestMessage> requestMessageHandler = new Handler<RequestMessage>() {
		@Override
		public void handle(RequestMessage event) {
			if (instanceRunning()) {
				Request req = event.getRequest();
				logger.debug("Received request for block " + req);
				requestQueue.add(req);
			}
		}
	};
	
	/**
	 * This event handler is triggered periodically to process request queue
	 */
	Handler<ProcessRequestQueue> processRequestQueueHandler = new Handler<ProcessRequestQueue>() {
		@Override
		public void handle(ProcessRequestQueue event) {
			if (instanceRunning() && acceptRequest) {
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
			}
			scheduleProcessingRequestQueue();
		}
	};
	
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

	Handler<Ready> memoryReadySignalHandler = new Handler<Ready>() {
		@Override
		public void handle(Ready event) {
			numberOfDevicesLoaded++;
			logger.debug("Received ready signal from " + event.getDevice());
			if (instanceRunning()) osStarted();
		}
	};

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
	Handler<TransferingFinished> transferingFinishedHandler = new Handler<TransferingFinished>() {
		@Override
		public void handle(TransferingFinished event) {
			if (instanceRunning()) {
				Process process = pt.get(event.getPid());
				if (process != null) {
					Request request = process.getRequest();
					gui.decreaseNrDownloadersFor(request.getBlockId());
					currentTransfers.remove(event.getTimeoutId());
					informDownloader(event, process, request);
					removeFromProcessTable(event.getPid());
					endProcessOnCPU(event.getPid());
					if (currentTransfers.size() != 0) {
						cancelAllPreviousTimers();
						rescheduleAllTimers(BANDWIDTH/currentTransfers.size(), System.currentTimeMillis());
					}
				}
				checkIfCanAcceptRequest();
			}
		}

		private void informDownloader(TransferingFinished event, Process process, Request request) {
			if (request.getDestinationNode() != null) {
				logger.info("Rebalancing finished for " + event.getPid());
				trigger(new BlockTransfered(self, request.getDestinationNode(), process.getBlockSize()), network);
			} else {
				logger.debug("Transfering finished for " + event.getPid() );
				trigger(new RequestDone(self, cloudProvider, request.getId()), network);
			}
		}

		private void checkIfCanAcceptRequest() {
			if (!acceptRequest) {
				boolean found = false;
				for (String pid : pt.keySet()) {
					if (pt.get(pid).getRequest().getDestinationNode() != null) {
						found = true;
						break;
					}
				}
				if (found == false)
					acceptRequest = true;
			}
			trigger(new MemoryCheckOperation(), cpu);
		}
	};
	
	/**
	 * This means that the block is read from the disk into memory. Now we can transfer
	 * it into the network
	 */
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
	Handler<WaitTimeout> waitTimeoutHandler = new Handler<WaitTimeout>() {
		@Override
		public void handle(WaitTimeout event) {
			if (!instanceRunning()) {
				waitForSystemStartUp();
			} else {
				scheduleProcessingRequestQueue();
				scheduleCPULoadPropagationToCloudProvider();
			}
		}
	};

	/**
	 * This handler is triggered upon receiving a shutDown request from cloudProvider
	 */
	Handler<ShutDown> shutDownHandler = new Handler<ShutDown>() {
		@Override
		public void handle(ShutDown event) {
			if (event.getSource().equals(cloudProvider)) {
				logger.debug("Time to die forever :(");
				gui.dispose();
				trigger(new ShutDownAck(self, event.getSource(), node), network);
				scheduleDeath();
			} else {
				logger.warn("Oh oh! Someone is trying to kill me!");
			}
		}
	};
	
	/**
	 * This is for scheduling a complete shut down if the CloudProvider
	 * was not able to shut down this instance completely (because of port binding)
	 */
	Handler<Death> deathHandler = new Handler<Death>() {
		@Override
		public void handle(Death event) {
			System.exit(0);
		}
	};

	/**
	 * This handler is triggered when receives a heatBeat message from HealthChecker
	 */
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
	Handler<PropagateCPULoad> propagateCPULoadHandler = new Handler<PropagateCPULoad>() {
		@Override
		public void handle(PropagateCPULoad event) {
			if (instanceRunning()) {
				trigger(new MyCPULoad(self, cloudProvider, node, currentCpuLoad), network);
				trigger(new MemoryCheckOperation(), cpu);
				scheduleCPULoadPropagationToCloudProvider();
			}
		}
	};

	/**
	 * This handler is for getting CPU Load chart from CPU
	 */
	Handler<SnapshotRequest> snapshotRequestHandler = new Handler<SnapshotRequest>() {
		@Override
		public void handle(SnapshotRequest event) {
			if (instanceRunning()) {
				InstanceSnapshot snapshot = new InstanceSnapshot(lastSnapshotID++);
				snapshot.addCPULoadChart(event.getCPULoadChart());
				snapshot.addBandwidthChart(getBandwidthChart());
				gui.addSnapshot(snapshot);
			}
		}
	};
	
	/**
	 * This handler is triggered by Sensor component
	 */
	Handler<Monitor> monitorHandler = new Handler<Monitor>() {
		@Override
		public void handle(Monitor event) {
			if (instanceRunning()) {
				logger.info("Recieved Monitor from Sensor");
				MonitorPacket monitorPacket = new MonitorPacket(currentCpuLoad, currentBandwidth);
				trigger(new MonitorResponse(self, event.getSource(), monitorPacket), network);
			}
		}
	};
	
	/**
	 * This handler is triggered by Cloud Provider 
	 */
	Handler<RestartInstance> restartInstanceHandler = new Handler<RestartInstance>() {
		@Override
		public void handle(RestartInstance event) {
			if (instanceRunning()) {
				restartInstance();
			}
		}
	};

	/**
	 * This handler sends a request to another instance asking for all the blocks that it owns.
	 */
	Handler<RebalanceRequest> rebalanceRequestHandler = new Handler<RebalanceRequest>() {
		@Override
		public void handle(RebalanceRequest event) {
			Block block = findRequestedBlock(event.getBlockId());
			trigger(new RebalanceResponse(self, event.getSource(), block), network);
			acceptRequest = false;
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
	Handler<BlockTransfered> blockTransferedHandler = new Handler<BlockTransfered>() {
		@Override
		public void handle(BlockTransfered event) {
			String process = pt.keySet().iterator().next();
			pt.remove(process);
			endProcessOnCPU(process);
			trigger(new DiskWriteOperation(event.getBlockSize()), cpu);
			calculateNewBandwidth();
			addToBandwidthDiagram(currentBandwidth);
			if (pt.size() == 0) {
				logger.info("Starting with " + blocks.size() + " block(s) in hand");
				LoadBlock load = new LoadBlock(blocks);
				trigger(load, disk);
				trigger(new BlocksAck(self, cloudProvider, node), network);
				gui.initializeDataBlocks(blocks);
				trigger(new InstanceStarted(self, cloudProvider, node), network);				
			}
		}

		private void calculateNewBandwidth() {
			if (pt.size() == 0 ) {
				currentBandwidth = BANDWIDTH;
			} else {
				currentBandwidth = BANDWIDTH / pt.size();
			}
		}
	};

	private void osStarted() {
		logger.debug("OS with kernel " + uname_r + " started");
		gui.decorateSystemStarted();
	}

	protected Block findRequestedBlock(String blockId) {
		for (Block block : blocks) {
			if (block.getName().equals(blockId))
				return block;
		}
		return null;
	}

	protected void scheduleCPULoadPropagationToCloudProvider() {
		ScheduleTimeout st = new ScheduleTimeout(CPU_LOAD_PROPAGATION_INTERVAL);
		st.setTimeoutEvent(new PropagateCPULoad(st));
		trigger(st, timer);
		gui.createBandwidthDiagram(getBandwidthChart());
	}

	protected void scheduleProcessingRequestQueue() {
		ScheduleTimeout st = new ScheduleTimeout(REQUEST_QUEUE_PROCESSING_INTERVAL);
		st.setTimeoutEvent(new ProcessRequestQueue(st));
		trigger(st, timer);		
	}

	protected void loadKernel() {
		gui.setTitle(node.getNodeName() + "@" + node.getIP() + ":" + node.getPort());
		gui.decorateWhileSystemStartUp();
		enabled = false;
		kernel.init(nodeConfiguration.getCpuSpeedInstructionPerSecond());
		enabled = true;
	}

	protected void scheduleDeath() {
		ScheduleTimeout st = new ScheduleTimeout(1000);
		st.setTimeoutEvent(new Death(st));
		trigger(st, timer);		
	}

	protected void waitForSystemStartUp() {
		ScheduleTimeout st = new ScheduleTimeout(WAIT);
		st.setTimeoutEvent(new WaitTimeout(st));
		trigger(st, timer);		
	}

	protected void readFromDiskIntoMemory(BlockResponse response) {
		Process process = response.getProcess();
		Block block = response.getBlock();
		process.setBlockSize(block.getSize());
		ScheduleTimeout st = new ScheduleTimeout(OperationDuration.getDiskReadDuration(CPU.CPU_CLOCK, response.getBlock().getSize()));
		ReadDiskFinished rd = new ReadDiskFinished(st);
		rd.setPid(process.getPid());
		st.setTimeoutEvent(rd);
		trigger(st, timer);		
	}

	protected synchronized void scheduleTransferForBlock(Process process) {
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
	}

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
			TransferingFinished tt = new TransferingFinished(st);
			tt.setPid(p.getPid());
			st.setTimeoutEvent(tt);
			ct.put(st.getTimeoutEvent().getTimeoutId(), p.getPid());
			trigger(st, timer);
		}
		
		currentTransfers.clear();
		currentTransfers.putAll(ct);
	}

	private void scheduleTimeoutFor(Process process, long bandwidth) {
		trigger(new MemoryCheckOperation(), cpu);
		long transferDelay = 1000 * process.getBlockSize() / bandwidth ;
		ScheduleTimeout st = new ScheduleTimeout(1000 * process.getBlockSize() / bandwidth );
		TransferingFinished tt = new TransferingFinished(st);
		tt.setPid(process.getPid());
		st.setTimeoutEvent(tt);
		UUID timeoutId = st.getTimeoutEvent().getTimeoutId();
		currentTransfers.put(timeoutId, process.getPid());
		process.setCurrentBandwidth(bandwidth).setRemainingBlockSize(process.getBlockSize()).setSnapshot(System.currentTimeMillis()).setTimeout(transferDelay);
		pt.put(process.getPid(), process);
		trigger(st, timer);
	}

	private void cancelAllPreviousTimers() {
		logger.debug("Cancelling previous schedulers...");
		for (Entry<UUID, String> en : currentTransfers.entrySet()) {
			trigger(new MemoryCheckOperation(), cpu);
			CancelTimeout cancel = new CancelTimeout(en.getKey());
			trigger(cancel, timer);
		}
	}

	protected void startProcessForRequest(Request event) {
		Process p = new Process(event);
		startTransferProcessOnCPU(p);
		addToProcessTable(p);
		checkMemory(p);		
		gui.increaseNrDownloadersFor(event.getBlockId());
	}

	private void checkMemory(Process p) {
		RequestBlock rBlock = new RequestBlock(p);
		trigger(rBlock, memory);
		trigger(new MemoryCheckOperation(), cpu);
	}

	private void addToProcessTable(Process p) {
		pt.put(p.getPid(), p);		
	}
	
	private void removeFromProcessTable(String pid) {
		pt.remove(pid);		
	}

	private void startTransferProcessOnCPU(Process p) {
		StartProcess process = new StartProcess(p);
		trigger(process, cpu);		
	}
	
	private void endProcessOnCPU(String pid) {
		EndProcess process = new EndProcess(pid);
		trigger(process, cpu);		
	}

	protected void loadBlocksToDisk(OSInit event) {
		if (event.getBlocks() != null ) {
			blocks = event.getBlocks();
			logger.info("Starting with " + blocks.size() + " block(s) in hand");
			LoadBlock load = new LoadBlock(blocks);
			trigger(load, disk);
			trigger(new BlocksAck(self, cloudProvider, node), network);
			gui.initializeDataBlocks(blocks);
			trigger(new InstanceStarted(self, cloudProvider, node), network);
		} else {
			logger.warn("I should get blocks from " + event.getNodeConfiguration().getDataBlocksMap().size() + " other instance(s)");
			Map<String, Address> dataBlocks = event.getNodeConfiguration().getDataBlocksMap();
			for (String blockId : dataBlocks.keySet()) {
				trigger(new RebalanceRequest(self, dataBlocks.get(blockId), blockId), network);
			}
			addToBandwidthDiagram(BANDWIDTH);
		}
	}
	
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
		if ( numberOfDevices != numberOfDevicesLoaded ) 
			return false;
		return enabled;
	}
	
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
		scheduleRestart();		
	}
	
	private void scheduleRestart() {
		ScheduleTimeout st = new ScheduleTimeout(RESTART_PERIOD);
		st.setTimeoutEvent(new OSRestartTimeout(st));
		trigger(st, timer);		
	}

}
