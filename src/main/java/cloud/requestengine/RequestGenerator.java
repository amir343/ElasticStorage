package cloud.requestengine;

import cloud.common.Generator;
import cloud.common.RequestEngineTimeout;
import cloud.common.SendRawData;
import cloud.elb.BlocksActivated;
import cloud.gui.CloudGUI;
import instance.common.Block;
import instance.common.Request;
import logger.Logger;
import logger.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;
import statistics.distribution.Distribution;

import java.util.*;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-30
 *
 */

public class RequestGenerator extends ComponentDefinition {
	
	private CloudGUI gui = CloudGUI.getInstance();
	private Logger logger = LoggerFactory.getLogger(RequestGenerator.class, gui);
	private Distribution distribution;
	
	// Ports
	Negative<Generator> generator = provides(Generator.class);
	Positive<Timer> timer = requires(Timer.class);
	
	private Map<UUID, RequestStatistic> currentRequests = new HashMap<UUID, RequestStatistic>();
	private List<RequestStatistic> completedRequest = new ArrayList<RequestStatistic>();
	private List<RequestStatistic> completedRequestClone = new ArrayList<RequestStatistic>();
	private ResponseTimeService responseTimeService = ResponseTimeService.getInstance();
	private List<Integer> throughputCollection = new ArrayList<Integer>();
	private Set<Block> blocks = new HashSet<Block>();
	private List<UUID> timerIds = new ArrayList<UUID>();
	private RequestGenerator reqGen;
	private boolean running = false;
	private long RT_COLLECTION_TIMEOUT = 5000;
	
	public RequestGenerator() {
		reqGen = this;
		
		subscribe(initHandler, generator);
		subscribe(downloadStartedHandler, generator);
		subscribe(sendRawDataHandler, generator);
		subscribe(blocksActivatedHandler, generator);
		
		subscribe(requestEngineTimeout, timer);
		subscribe(RTCollectionTimeoutHandler, timer);
	}
	
	Handler<RequestGeneratorInit> initHandler = new Handler<RequestGeneratorInit>() {
		@Override
		public void handle(RequestGeneratorInit event) {
			setupGUIConnection();
			logger.info("Request Engine started...");
		}
	};
	
	/**
	 * This handler is triggered according to the current distribution and prepares and sends requests for all data blocks
	 */
	Handler<RequestEngineTimeout> requestEngineTimeout = new Handler<RequestEngineTimeout>() {
		@Override
		public void handle(RequestEngineTimeout event) {
			synchronized (timerIds) {
				timerIds.remove(event.getTimeoutId());
			}
			for (Block block : blocks) {
				logger.debug("Preparing request for block "+ block);
				Request request = new Request(UUID.randomUUID().toString(), block.getName());
				RequestStatistic stat = new RequestStatistic();
				stat.setStart(System.currentTimeMillis());
				currentRequests.put(UUID.fromString(request.getId()), stat);
				trigger(request, generator);
			}
			if (running) scheduleRequestGeneratorEngine();			
		}
	};
	
	/**
	 * This handler is triggered when a transfer finishes and calculates the response time for further presentation
	 */
	Handler<DownloadStarted> downloadStartedHandler = new Handler<DownloadStarted>() {
		@Override
		public void handle(DownloadStarted event) {
			UUID id = UUID.fromString(event.getRequestID());
			currentRequests.get(id).setEnd(System.currentTimeMillis());
			completedRequest.add(currentRequests.get(id));
			completedRequestClone.add(currentRequests.get(id));
			currentRequests.remove(id);			
		}
	};
	
	/**
	 * This handler is triggered periodically to draw response time scatter plot in the GUI
	 */
	Handler<RTCollectionTimeout> RTCollectionTimeoutHandler = new Handler<RTCollectionTimeout>() {
		@Override
		public void handle(RTCollectionTimeout event) {
			if (completedRequest.size() != 0) {
				responseTimeService.add(completedRequest);
				completedRequest.clear();
				gui.updateResponseTime();
			}
			scheduleResponseTimeCollector();
		}
	};
	
	/**
	 * This handler is triggered when cloudAPI request the average response time
	 */
	Handler<SendRawData> sendRawDataHandler = new Handler<SendRawData>() {
		@Override
		public void handle(SendRawData event) {
			double averageResponseTime = calculateAverageResponseTime();
			double averageThroughput = calculateAverageThroughput();
			
			event.setAverageResponseTime(averageResponseTime);
			event.setAverageThroughput(averageThroughput);
			
			trigger(event, generator);			
		}
	};
	
	/**
	 * This handler is triggered when the engine receives blocks that are activated from ELB
	 */
	Handler<BlocksActivated> blocksActivatedHandler = new Handler<BlocksActivated>() {
		@Override
		public void handle(BlocksActivated event) {
			blocks.addAll(event.getBlocks());
		}
	};

	protected void scheduleRequestGeneratorEngine() {
		long timeout = distribution.getNextValue();
		throughputCollection.add((int) (timeout/1000));
		logger.debug("Next request will be sent in " + timeout + " (ms)");
		if (timeout > 0 && timeout != Long.MAX_VALUE) {
			ScheduleTimeout st = new ScheduleTimeout(timeout);
			st.setTimeoutEvent(new RequestEngineTimeout(st));
			UUID id = st.getTimeoutEvent().getTimeoutId();
			synchronized (timerIds) {
				timerIds.add(id);
			}
			trigger(st, timer);
		}
	}

	private double calculateAverageResponseTime() {
		Long responseTimeSum = 0L;
		for (RequestStatistic req : completedRequestClone) {
			responseTimeSum += req.getResponseTime();
		}
		double mean;
		if (completedRequestClone.size() == 0)
			mean = 0.0;
		else
			 mean = responseTimeSum.doubleValue()/completedRequestClone.size();
		completedRequestClone.clear();
		return mean;
	}

	protected double calculateAverageThroughput() {
		int throughputSum = 0;
		double mean;
		for (Integer tp : throughputCollection) {
			throughputSum += tp;
		}
		if (throughputCollection.size() == 0)
			mean = 0;
		else
			mean = throughputSum/throughputCollection.size();
		throughputCollection.clear();
		return mean;
	}

	protected void setupGUIConnection() {
		gui.setRequestGenerator(reqGen);		
	}

	public void updateDistribution(Distribution currentDistribution) {
		this.distribution = currentDistribution;
		cancelPreviousTimers();
		scheduleRequestGeneratorEngine();
		scheduleResponseTimeCollector();
		running = true;
	}
	
	private void scheduleResponseTimeCollector() {
		ScheduleTimeout st = new ScheduleTimeout(RT_COLLECTION_TIMEOUT);
		st.setTimeoutEvent(new RTCollectionTimeout(st));
		trigger(st, timer);		
	}

	public void stopCurrentDistribution() {
		running = false;
	}

	private void cancelPreviousTimers() {
		for (UUID id : timerIds) {
			CancelTimeout cancel = new CancelTimeout(id);
			trigger(cancel, timer);
		}
		synchronized (timerIds) {
			timerIds.clear();
		}
	}

}
