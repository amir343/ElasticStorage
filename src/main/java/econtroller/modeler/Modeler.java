package econtroller.modeler;

import common.GUI;

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
import cloud.common.RequestTrainingData;
import cloud.common.TrainingData;
import econtroller.gui.ControllerGUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-05-08
 *
 */

public class Modeler extends ComponentDefinition {
	

	private Logger logger = LoggerFactory.getLogger(Modeler.class, ControllerGUI.getInstance());
	
	// Ports
	Negative<ModelPort> modeler = provides(ModelPort.class);
	Positive<Network> network = requires(Network.class);
	Positive<Timer> timer = requires(Timer.class);

	// Variables
	private static final long SAMPLING_INTERVAL = 30000;
	private Address cloudProvider;
	private Address self;

	private ControllerGUI gui;

	public Modeler() {
		gui = ControllerGUI.getInstance();
		gui.setModeler(this);
		
		subscribe(initHandler, control);
		
		subscribe(startModelerHandler, modeler);
		
		subscribe(sampleTraingingDataHandler, timer);
		
		subscribe(trainingDataHanler, network);
	}
	
	Handler<ModelerInit> initHandler = new Handler<ModelerInit>() {
		@Override
		public void handle(ModelerInit event) {
			self = event.getSelf();
			logger.info("Modeler started...");
		}
	};	
	
	/**
	 * This handler is triggered when the Controller issues a START signal to Modeler
	 */
	Handler<StartModeler> startModelerHandler = new Handler<StartModeler>() {
		@Override
		public void handle(StartModeler event) {
			logger.info("Modeler received START signal from controller");
			cloudProvider = event.getCloudProviderAddress();
		} 
	};
	
	/**
	 * This handler is responsible for requesting training data from cloudProvider
	 */
	Handler<SampleTrainingData> sampleTraingingDataHandler = new Handler<SampleTrainingData>() {
		@Override
		public void handle(SampleTrainingData event) {
			trigger(new RequestTrainingData(self, cloudProvider), network);
			scheduleSampling();
		}
	};
	
	/**
	 * This handler is responsible for sorting out the response (raw data tuples) it receives from cloudProvider
	 */
	Handler<TrainingData> trainingDataHanler = new Handler<TrainingData>() {
		@Override
		public void handle(TrainingData event) {
			if (event.getBandwidthMean() != null) {
				logger.debug("Bandwidth: <" + event.getNrNodes() + ", " + event.getBandwidthMean() + ">");
			} 
			if (event.getLoadMean() != null) {
				logger.debug("CPU Load: <" + event.getNrNodes() + ", " + event.getLoadMean() + ">");
			}
			if (event.getResponseTimeMean() != null) {
				logger.debug("ResponseTime: <" + event.getNrNodes() + ", " + event.getResponseTimeMean() + ">");
			}
			if (event.getTotalCost() != null) {
				logger.debug("TotalCost: <" + event.getNrNodes() + ", " + event.getTotalCost() + ">");
			}
		}
	};

	protected void scheduleSampling() {
		ScheduleTimeout st = new ScheduleTimeout(SAMPLING_INTERVAL);
		st.setTimeoutEvent(new SampleTrainingData(st));
		trigger(st, timer);
		
	}

	public void startModeler() {
		trigger(new RequestTrainingData(self, cloudProvider), network);
		scheduleSampling();		
	}
}
