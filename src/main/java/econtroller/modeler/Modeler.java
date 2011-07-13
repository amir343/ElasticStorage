package econtroller.modeler;

import cloud.common.RequestTrainingData;
import cloud.common.TrainingData;
import econtroller.controller.NewNodeRequest;
import econtroller.controller.RemoveNode;
import econtroller.gui.ControllerGUI;
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

import java.util.UUID;

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
	private long SAMPLING_INTERVAL = 10000;
	private long ORDER_INTERVAL = 90000;
	private Address cloudProvider;
	private Address self;
	private ControllerGUI gui;
	private int maxNrInstances = 8;
	private int minNrInstances = 2;
	private int currentlyOrdered = 0;
	private boolean add = true;
	private long start;
	private UUID samplingTimeoutId;
	private UUID actuationTimeoutId;
	private int snapshotId = 0;
	
	private DataFilesGenerator generator = new DataFilesGenerator();

	private XYSeries nrInstancesSeries = new XYSeries("# of Instances");
	private XYSeries rtSeries = new XYSeries("Average ResponseTime");
	private XYSeries tpSeries = new XYSeries("Average Throughput");
	private XYSeries cpuSeries = new XYSeries("Average CPU Load");
	private XYSeries costSeries = new XYSeries("Total Cost");
	private XYSeries bandwidthSeries = new XYSeries("Average Bandwidth");
    private boolean orderingEnabled = true;


    public Modeler() {
		gui = ControllerGUI.getInstance();
		gui.setModeler(this);
		
		subscribe(initHandler, control);
		
		subscribe(startModelerHandler, modeler);
		
		subscribe(sampleTraingingDataHandler, timer);
		subscribe(instanceCreationHandler, timer);
		
		subscribe(trainingDataHandler, network);
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
            if (orderingEnabled) {
                if (add) {
                    if (currentlyOrdered < maxNrInstances ) {
                        requestNewNode();
                    } else {
                        add = false;
                        removeNode();
                    }
                } else {
                    if (currentlyOrdered > minNrInstances ) {
                        removeNode();
                    } else {
                        generator.dump();
                        add = true;
                        requestNewNode();
                    }
                }
            }
			scheduleAcutation();
		}
	};
	
	/**
	 * This handler is responsible for sorting out the response (raw data tuples) it receives from cloudProvider
	 */
	Handler<TrainingData> trainingDataHandler = new Handler<TrainingData>() {
		@Override
		public void handle(TrainingData event) {
/*
			logger.info("Training data is received: " + event.toString());
*/
			generator.add(event);
			bandwidthSeries.add(System.currentTimeMillis()-start, event.getBandwidthMean());
			gui.updateBandwidthChart(getBandwidthChart());
			cpuSeries.add(System.currentTimeMillis()-start, event.getCpuLoadMean());
			gui.updateCpuLoadChart(getCPUChart());
			rtSeries.add(System.currentTimeMillis()-start, event.getResponseTimeMean());
			gui.updateResponseTimeChart(getResponseTimeChart());
			costSeries.add(System.currentTimeMillis()-start, event.getTotalCost());
			gui.updateTotalCostChart(getTotalCostChart());
			tpSeries.add(System.currentTimeMillis()-start, event.getThroughputMean());
			gui.updateThroughputChart(getAverageThroughputChart());
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
		generator.dump();
		return "Done";
	}

	protected void removeNode() {
		currentlyOrdered--;
		trigger(new RemoveNode(self, cloudProvider, 1), network);
	}

	protected void requestNewNode() {
		currentlyOrdered++;
		trigger(new NewNodeRequest(self, cloudProvider, 1), network);
	}

	public void startModeler(boolean orderingEnabled, int maximumNrInstances, int minimumNrInstances, int sampleInterval, int orderInterval) {
        this.orderingEnabled = orderingEnabled;
		start = System.currentTimeMillis();
		trigger(new RequestTrainingData(self, cloudProvider), network);
		maxNrInstances = maximumNrInstances;
		minNrInstances = minimumNrInstances;
		currentlyOrdered = minimumNrInstances;
		SAMPLING_INTERVAL = sampleInterval * 1000;
		ORDER_INTERVAL = orderInterval * 1000;
		scheduleSampling();
		scheduleAcutation();
	}
	
	public void stopModeler() {
		trigger(new CancelTimeout(actuationTimeoutId), timer);
		trigger(new CancelTimeout(samplingTimeoutId), timer);
	}

	
	private void scheduleAcutation() {
		ScheduleTimeout st = new ScheduleTimeout(ORDER_INTERVAL);
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
	
	public void takeSnapshot() {
		snapshotId++;
		ModelerSnapshot snapshot = new ModelerSnapshot(snapshotId);
		snapshot.setCpuChart(getCPUChart());
		snapshot.setResponseTimeChart(getResponseTimeChart());
		snapshot.setBandwidthChart(getBandwidthChart());
		snapshot.setNrInstanceChart(getNrInstancesChart());
		snapshot.setTotalCostChart(getTotalCostChart());
		snapshot.setAverageThroughputChart(getAverageThroughputChart());
		gui.addSnapshot(snapshot);
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
