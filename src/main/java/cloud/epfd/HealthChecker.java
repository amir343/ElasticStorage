package cloud.epfd;

import java.util.HashSet;
import java.util.Set;

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
import cloud.common.Alive;
import cloud.common.ConsiderInstance;
import cloud.common.EPFD;
import cloud.common.HeartbeatMessage;
import cloud.common.HeartbeatTimeout;
import cloud.common.InstanceKilled;
import cloud.common.Restore;
import cloud.common.Suspect;
import cloud.gui.CloudGUI;

import com.google.common.collect.Sets;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-27
 *
 * <code>HealthChecker</code> is a neat implementation of Eventual Perfect Failure Detector
 * with some optimizations within Cloud context. 
 *
 */

public class HealthChecker extends ComponentDefinition {
	
	// Ports
	Negative<EPFD> epfd = provides(EPFD.class);
	Positive<Timer> timer = requires(Timer.class);
	Positive<Network> network = requires(Network.class);

	private Set<Address> availableInstances = new HashSet<Address>();
	private Set<Address> alive = new HashSet<Address>();
	private Set<Address> suspect = new HashSet<Address>();
	private Set<Address> killedInstances = new HashSet<Address>();
	private long PERIOD = 5000;
	private long DELTA = 1000;
	protected Address self;
	private Logger logger;
	
	public HealthChecker() {
		subscribe(initHandler, epfd);
		subscribe(considerInstanceHandler, epfd);
		subscribe(instanceKilledHandler, epfd);		
		
		subscribe(heartbeatTimeoutHandler, timer);
		
		subscribe(aliveHandler, network);
	}
	
	Handler<HealthCheckerInit> initHandler = new Handler<HealthCheckerInit>() {
		@Override
		public void handle(HealthCheckerInit event) {
			logger = LoggerFactory.getLogger(HealthChecker.class, CloudGUI.getInstance());
			PERIOD = event.getPeriod() == 0 ? PERIOD : event.getPeriod();
			DELTA = event.getDelta() == 0 ? DELTA : event.getDelta();
			logger.info("HealthChecker started with Period=" + PERIOD + " and Delta=" + DELTA);
			self = event.getSelfAddress();
			startPeriodicHeartbeatTimer();
		}
	};
	
	/**
	 * This handler is triggered periodically to check the health of all available instances
	 */
	Handler<HeartbeatTimeout> heartbeatTimeoutHandler = new Handler<HeartbeatTimeout>() {
		@Override
		public void handle(HeartbeatTimeout event) {
			if (availableInstances.size() != 0) {
				if (Sets.intersection(alive, suspect).size() != 0) 
					PERIOD += DELTA;
				for(Address node : availableInstances) {
					if ( !alive.contains(node) && !suspect.contains(node)) {
						if (!killedInstances.contains(node)) {
							suspect.add(node);
							trigger(new Suspect(node), epfd);
							logger.info("Node " + node + " is suspected");
						}
					} else if ( alive.contains(node) && suspect.contains(node)) {
						suspect.remove(node);
						trigger(new Restore(node), epfd);
						logger.info("Node " + node + " is restored");
					}
					trigger(new HeartbeatMessage(self, node), network);
				}
				alive.clear();
			}
			startPeriodicHeartbeatTimer();			
		}
	};
	
	/**
	 * This handler is triggered when a node responds back to a previously sent Heatbeat message
	 */
	Handler<Alive> aliveHandler = new Handler<Alive>() {
		@Override
		public void handle(Alive event) {
			alive.add(event.getSource());
			availableInstances.add(event.getSource());
		}
	};
	
	/**
	 * This handler is triggered when a new node is started and cloud Provider discovers its existence
	 */
	Handler<ConsiderInstance> considerInstanceHandler = new Handler<ConsiderInstance>() {
		@Override
		public void handle(ConsiderInstance event) {
			trigger(new HeartbeatMessage(self, event.getAddress()), network);
			logger.debug("I will consider the health for " + event.getNode());
		}
	};
	
	/**
	 * This handler is triggered when one instance is killed or shut down so the EPFD would not consider checking
	 * its health
	 */
	Handler<InstanceKilled> instanceKilledHandler = new Handler<InstanceKilled>() {
		@Override
		public void handle(InstanceKilled event) {
			availableInstances.remove(event.getAddress());
			alive.remove(event.getAddress());
			killedInstances.add(event.getAddress());
			logger.debug("I will not consider the health for " + event.getNode());
		}
	};

	protected void startPeriodicHeartbeatTimer() {
		ScheduleTimeout st = new ScheduleTimeout(PERIOD);
		st.setTimeoutEvent(new HeartbeatTimeout(st));
		trigger(st, timer);
	} 

}
