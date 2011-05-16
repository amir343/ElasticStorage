package econtroller.modeler;

import java.util.UUID;

import logger.Logger;
import logger.LoggerFactory;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
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
import cloud.common.RequestTrainingData;
import cloud.common.TrainingData;
import econtroller.controller.NewNodeRequest;
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
	private static final long SAMPLING_INTERVAL = 10000;
	private static final long ACUATION_INTERVAL = 90000;
	private Address cloudProvider;
	private Address self;
	private ControllerGUI gui;
	private int nrInstances = 8;
	private int currentlyOrdered = 0;
	private boolean add = true;
	private long start;
	private UUID samplingTimeoutId;
	private UUID actuationTimeoutId;
	
	private DataFilesGenerator generator = new DataFilesGenerator();

	private XYSeries nrInstancesSeries = new XYSeries("# of Instances");
	private XYSeries rtSeries = new XYSeries("Average ResponseTime");
	private XYSeries tpSeries = new XYSeries("Average Throughput");
	private XYSeries cpuSeries = new XYSeries("Average CPU Load");
	private XYSeries costSeries = new XYSeries("Total Cost");
	private XYSeries bandwidthSeries = new XYSeries("Average Bandwidth");
	
	
	public Modeler() {
		gui = ControllerGUI.getInstance();
		gui.setModeler(this);
		
		subscribe(initHandler, control);
		
		subscribe(startModelerHandler, modeler);
		
		subscribe(sampleTraingingDataHandler, timer);
		subscribe(instanceCreationHandler, timer);
		
		subscribe(trainingDataHanler, network);
	}
	
	Handler<ModelerInit> initHandler = new Handler<ModelerInit>() {
		@Override
		public void handle(ModelerInit event) {
			self = event.getSelf();
			logger.info("Modeler started...");
			gui.updateBandwidthChart(getBandwidthChart());
			gui.updateCpuLoadChart(getCPUChart());
			gui.updateResponseTimeChart(getResponseTimeChart());
			gui.updateTotalCostChart(getTotalCostChart());
			gui.updateNrOfInstancesChart(getNrInstancesChart());
			gui.updateThroughputChart(getAverageThroughputChart());
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
	 * This handler is responsible for adding and removing instances so the modeler can have a range for the system inputs
	 */
	Handler<InstanceCreation> instanceCreationHandler = new Handler<InstanceCreation>() {
		@Override
		public void handle(InstanceCreation event) {
			if (add) {
				if (currentlyOrdered < nrInstances-1) {
					requestNewNode();
				} else {
					add = false;
					removeNode();
				}
			} else {
				if (currentlyOrdered > 0) {
					removeNode();
				} else {
					add = true;
					estimateParameters();
					requestNewNode();
				}
			}
			scheduleAcutation();
		}
	};
	
	/**
	 * This handler is responsible for sorting out the response (raw data tuples) it receives from cloudProvider
	 */
	Handler<TrainingData> trainingDataHanler = new Handler<TrainingData>() {
		@Override
		public void handle(TrainingData event) {
			logger.info("Training data is received: " + event.toString());
			generator.add(event);
			if (event.getBandwidthMean() != null) {
				bandwidthSeries.add(System.currentTimeMillis()-start, event.getBandwidthMean());
				gui.updateBandwidthChart(getBandwidthChart());
			} 
			if (event.getCPULoadMean() != null) {
				cpuSeries.add(System.currentTimeMillis()-start, event.getCPULoadMean());
				gui.updateCpuLoadChart(getCPUChart());
			}
			if (event.getResponseTimeMean() != null) {
				rtSeries.add(System.currentTimeMillis()-start, event.getResponseTimeMean());
				gui.updateResponseTimeChart(getResponseTimeChart());
			}
			if (event.getTotalCost() != null) {
				costSeries.add(System.currentTimeMillis()-start, event.getTotalCost());
				gui.updateTotalCostChart(getTotalCostChart());
			}
			if (event.getThroughputMean() != null) {
				tpSeries.add(System.currentTimeMillis()-start, event.getThroughputMean());
				gui.updateThroughputChart(getAverageThroughputChart());
			}
			nrInstancesSeries.add(System.currentTimeMillis()-start, event.getNrNodes());
			gui.updateNrOfInstancesChart(getNrInstancesChart());
		}
	};

	protected void scheduleSampling() {
		ScheduleTimeout st = new ScheduleTimeout(SAMPLING_INTERVAL);
		st.setTimeoutEvent(new SampleTrainingData(st));
		samplingTimeoutId = st.getTimeoutEvent().getTimeoutId();
		trigger(st, timer);
	}

	public String estimateParameters() {
//		String s1 = "cpu: <a,b> = <" + cpuLSR.get_a() + ", " + cpuLSR.get_b() + "> [numberOfSamples= " + cpuLSR.getNumberOfSamples() + "]";
//		String s2 = "bandwidth: <a,b> = <" + bandwidthLSR.get_a() + ", " + bandwidthLSR.get_b() + "> [numberOfSamples= " + bandwidthLSR.getNumberOfSamples() + "]";
//		String s3 = "response time: <a,b> = <" + responseTimeLSR.get_a() + ", " + responseTimeLSR.get_b() + "> [numberOfSamples= " + responseTimeLSR.getNumberOfSamples() + "]";
//		String s4 = "total cost: <a,b> = <" + costLSR.get_a() + ", " + costLSR.get_b() + "> [numberOfSamples= " + costLSR.getNumberOfSamples() + "]";
//		StringBuilder sb = new StringBuilder();
//		logger.warn(s1);
//		logger.warn(s2);
//		logger.warn(s3);
//		logger.warn(s4);
//		logger.warn("cpu table\n" + cpuLSR.print());
//		logger.warn("bandwidth table\n" + bandwidthLSR.print());
//		logger.warn("cost table\n" + costLSR.print());
//		logger.warn("response time table\n" + responseTimeLSR.print());
//		sb.append(s1).append("\n");
//		sb.append(s2).append("\n");
//		sb.append(s3).append("\n");
//		sb.append(s4).append("\n");
//		return sb.toString();
		generator.dump();
		return "Done";
	}

	protected void removeNode() {
		currentlyOrdered--;
		trigger(new RemoveNode(self, cloudProvider), network);
	}

	protected void requestNewNode() {
		currentlyOrdered++;
		trigger(new NewNodeRequest(self, cloudProvider), network);
	}

	public void startModeler(int maximumNrInstances) {
		start = System.currentTimeMillis();
		trigger(new RequestTrainingData(self, cloudProvider), network);
		nrInstances = maximumNrInstances;		
		scheduleSampling();
		scheduleAcutation();
	}
	
	public void stopModeler() {
		trigger(new CancelTimeout(actuationTimeoutId), timer);
		trigger(new CancelTimeout(samplingTimeoutId), timer);
	}

	
	private void scheduleAcutation() {
		ScheduleTimeout st = new ScheduleTimeout(ACUATION_INTERVAL);
		st.setTimeoutEvent(new InstanceCreation(st));
		actuationTimeoutId = st.getTimeoutEvent().getTimeoutId();
		trigger(st, timer);		
	}

	private JFreeChart getCPUChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Average CPU Load", "Time (ms)", "Average CPU Load", getDataset(cpuSeries), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	private JFreeChart getResponseTimeChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Average Response Time", "Time (ms)", "Average Response Time (ms)", getDataset(rtSeries), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	private JFreeChart getBandwidthChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Average Bandwidth", "Time (ms)", "Average Bandwidth (B/s)", getDataset(bandwidthSeries), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	private JFreeChart getNrInstancesChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Number of Instances", "Time (ms)", "# of Instances", getDataset(nrInstancesSeries), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	private JFreeChart getTotalCostChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Total Cost", "Time (ms)", "Total Cost ($)", getDataset(costSeries), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	private JFreeChart getAverageThroughputChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Average Throughput", "Time (ms)", "Average Throughput (every seconds)", getDataset(tpSeries), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	private XYDataset getDataset(XYSeries series) {
		XYSeriesCollection dataSet = new XYSeriesCollection();
		dataSet.addSeries(series);
		return dataSet;
	}

	public void reset() {
		cpuSeries.clear();
		rtSeries.clear();
		bandwidthSeries.clear();
		nrInstancesSeries.clear();
		costSeries.clear();
		tpSeries.clear();
		
		generator.clear();
	}
	
}
