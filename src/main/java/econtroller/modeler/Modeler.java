package econtroller.modeler;

import cloud.common.RequestTrainingData;
import cloud.common.SLAViolation;
import cloud.common.StateVariables;
import cloud.common.TrainingData;
import cloud.elb.SenseData;
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
	private Long start = null;
	private UUID samplingTimeoutId;
	private UUID actuationTimeoutId;
	private int snapshotId = 0;
    private boolean orderingEnabled = true;
    private boolean snapshotTaken = false;
    private double takeSnapshotTime = 2050;

	private DataFilesGenerator2 generator = new DataFilesGenerator2();
    private XYSeries nrInstancesSeries = new XYSeries("# of Instances");
    private XYSeries rtSeries = new XYSeries("Average ResponseTime");
    private XYSeries tpSeries = new XYSeries("Average Throughput");
    private XYSeries cpuLoadSeries = new XYSeries("Average CPU Load");
    private XYSeries costSeries = new XYSeries("Total Cost");
    private XYSeries bandwidthSeries = new XYSeries("Average Bandwidth");


    public Modeler() {
		gui = ControllerGUI.getInstance();
		gui.setModeler(this);

		subscribe(initHandler, control);

		subscribe(startModelerHandler, modeler);
        subscribe(senseDataHandler, modeler);

		subscribe(sampleTrainingDataHandler, timer);
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
	Handler<SampleTrainingData> sampleTrainingDataHandler = new Handler<SampleTrainingData>() {
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
                        takeSnapshot();
                        add = true;
                        requestNewNode();
                    }
                }
            }
			scheduleActuation();
		}
	};

	/**
	 * This handler is responsible for sorting out the response (raw data tuples) it receives from cloudProvider
	 */
	Handler<TrainingData> trainingDataHandler = new Handler<TrainingData>() {
		@Override
		public void handle(TrainingData event) {
            logger.info("Training data: \n" + event.toString());
			generator.add(event);
            saveAndPlotData(event.getCpuLoadMean(),
                    event.getCpuLoadSTD(),
                    event.getBandwidthMean(),
                    event.responseTimeMean(),
                    event.periodicTotalCost(),
                    event.getThroughputMean(),
                    event.getNrNodes()
            );
            printViolations(event);
            printTotalCost(event);
            dumpAndSnapshot();
		}
	};

    /**
     * This handler is triggered only when the controller runs and it enables monitoring
     */
    Handler<SenseData> senseDataHandler = new Handler<SenseData>() {
        @Override
        public void handle(SenseData event) {
            if (start == null) start = System.currentTimeMillis();
            generator.add(event);
            saveAndPlotData(event.getCpuLoadMean(),
                    event.getCpuLoadSTD(),
                    event.getBandwidthMean(),
                    event.getResponseTimeMean(),
                    event.getPeriodicTotalCost(),
                    event.getThroughputMean(),
                    event.getNrNodes()
            );
            printViolations(event);
            printTotalCost(event);
            dumpAndSnapshot();
        }
    };

    private void dumpAndSnapshot() {
        if (!snapshotTaken) {
            if (logger.getTime() >= takeSnapshotTime) {
                snapshotTaken = true;
                generator.dump();
                takeSnapshot();
            }
        }
    }

    private void printTotalCost(StateVariables event) {
        logger.warn("Total Cost: $ " + event.getTotalCost());
    }

    private void printViolations(SLAViolation event) {
        logger.warn("Violations: {CPULoad: %" + event.getCpuLoadViolation() + ", ResponseTime: %" + event.getResponseTimeViolation() + ", Bandwidth: %" + event.getBandwidthViolation() + "}");
    }

    private void saveAndPlotData(double cpuLoad, double cpuSTD, double bandwidth, double responseTime, double totalCost, double throughPut, int numberOfNodes) {
        bandwidthSeries.add(System.currentTimeMillis()-start, bandwidth);
        gui.updateBandwidthChart(getBandwidthChart());
        cpuLoadSeries.add(System.currentTimeMillis()-start, cpuLoad);
        gui.updateCpuLoadChart(getCPUChart());
        rtSeries.add(System.currentTimeMillis()-start, responseTime);
        gui.updateResponseTimeChart(getResponseTimeChart());
        costSeries.add(System.currentTimeMillis()-start, totalCost);
        gui.updateTotalCostChart(getTotalCostChart());
        tpSeries.add(System.currentTimeMillis()-start, throughPut);
        gui.updateThroughputChart(getAverageThroughputChart());
        nrInstancesSeries.add(System.currentTimeMillis()-start, numberOfNodes);
        gui.updateNrOfInstancesChart(getNrInstancesChart());
    }

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
		scheduleActuation();
	}
	
	public void stopModeler() {
		trigger(new CancelTimeout(actuationTimeoutId), timer);
		trigger(new CancelTimeout(samplingTimeoutId), timer);
	}

	
	private void scheduleActuation() {
		ScheduleTimeout st = new ScheduleTimeout(ORDER_INTERVAL);
		st.setTimeoutEvent(new InstanceCreation(st));
		actuationTimeoutId = st.getTimeoutEvent().getTimeoutId();
		trigger(st, timer);		
	}

	private JFreeChart getCPUChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Average CPU Load", "Time (ms)", "Average CPU Load", getDataset(cpuLoadSeries), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	private JFreeChart getResponseTimeChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Average Response Time", "Time (ms)", "Average Response Time (ms)", getDataset(rtSeries), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	private JFreeChart getBandwidthChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Average Bandwidth per Download", "Time (ms)", "Bandwidth (B/s)", getDataset(bandwidthSeries), PlotOrientation.VERTICAL, true, true, false);
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
		snapshot.setAverageBandwidth(getBandwidthChart());
		snapshot.setNrInstanceChart(getNrInstancesChart());
		snapshot.setTotalCostChart(getTotalCostChart());
		snapshot.setAverageThroughputChart(getAverageThroughputChart());
		gui.addSnapshot(snapshot);
	}

	public void reset() {
		cpuLoadSeries.clear();
		rtSeries.clear();
		bandwidthSeries.clear();
		nrInstancesSeries.clear();
		costSeries.clear();
		tpSeries.clear();
		
		generator.clear();
	}
	
}
