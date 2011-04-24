package cloud.requestengine;

import instance.common.Block;
import instance.common.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import cloud.common.Generator;
import cloud.common.RequestEngineTimeout;
import cloud.gui.CloudGUI;

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
	private ResponseTimeService responseTimeService = ResponseTimeService.getInstance();
	protected List<Block> blocks;
	private List<UUID> timerIds = new ArrayList<UUID>();
	private RequestGenerator reqGen;
	private boolean running = false;
	private long RT_COLLECTION_TIMEOUT = 5000;
	
	public RequestGenerator() {
		reqGen = this;
		
		subscribe(initHandler, generator);
		subscribe(requestDoneHandler, generator);
		
		subscribe(requestEngineTimeout, timer);
		subscribe(RTCollectionTimeoutHandler, timer);
	}
	
	Handler<RequestGeneratorInit> initHandler = new Handler<RequestGeneratorInit>() {
		@Override
		public void handle(RequestGeneratorInit event) {
			blocks = event.getBlocks();
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
	Handler<RequestDone> requestDoneHandler = new Handler<RequestDone>() {
		@Override
		public void handle(RequestDone event) {
			UUID id = UUID.fromString(event.getRequestID());
			currentRequests.get(id).setEnd(System.currentTimeMillis());
			completedRequest.add(currentRequests.get(id));
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

	protected void scheduleRequestGeneratorEngine() {
		long timeout = distribution.getNextValue();
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
