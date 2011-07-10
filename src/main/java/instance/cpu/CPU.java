package instance.cpu;

import instance.common.*;
import instance.common.Ready.Device;
import instance.gui.InstanceGUI;
import instance.os.*;
import instance.os.Process;
import logger.Logger;
import logger.LoggerFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 * 
 * <code>CPU</code>
 * A component representing CPU in a cloud instance
 * 
 */

public class CPU extends ComponentDefinition {


	private Logger logger = LoggerFactory.getLogger(CPU.class, InstanceGUI.getInstance());
	
	// Ports
	Negative<CPUChannel> cpu = provides(CPUChannel.class);
	Positive<Timer> timer = requires(Timer.class);
	
	// Variables needed by CPU
	public static long CPU_CLOCK = 2000000000L; // 2 GHz 
	private static final long LOAD_CALC_INTERVAL = 5000;
	private static final long SAMPLER_INTERVAL = 500;
	private long startTime = System.currentTimeMillis();
	private XYSeriesCollection dataSet = new XYSeriesCollection();
	private XYSeries xySeries = new XYSeries("Load");
	private AtomicInteger tasks = new AtomicInteger();
	private ConcurrentMap<String, Process> pt = new ConcurrentHashMap<String, Process>();
	private List<Integer> loadSamples = new ArrayList<Integer>();
	private List<Double> loads = new ArrayList<Double>();
	protected boolean enabled = false;
	protected InstanceGUI gui = InstanceGUI.getInstance();
	
	public CPU() {
		tasks.set(0);
		subscribe(initHandler, control);
		
		subscribe(startProcessHandler, cpu);
		subscribe(endProcessHandler, cpu);
		subscribe(abstractOperationHandler, cpu);
		subscribe(snapshotRequestHandler, cpu);
		subscribe(restartSignalHandler, cpu);

		subscribe(loadCalculationTimeoutHandler, timer);
		subscribe(loadSamplerTimeoutHandler, timer);
		subscribe(operationFinishedTimeoutHandler, timer);
		subscribe(restartHandler, timer);
	}
	
	Handler<CPUInit> initHandler = new Handler<CPUInit>() {
		@Override
		public void handle(CPUInit event) {
			enabled = true;
			CPU_CLOCK = event.getNodeConfiguration().getCpuConfiguration().getCpuSpeedInstructionPerSecond();
			gui.updateCPUInfoLabel(event.getNodeConfiguration().getCpuConfiguration().getCpuSpeed() + " GHz");
			dataSet.addSeries(xySeries);
			printCPULog(event);
			sendReadySignal();
			scheduleLoadSampler(1000);
			scheduleLoadCalculation();
			gui.createCPULoadDiagram(getChart());
		}

		private void printCPULog(CPUInit event) {
			logger.raw(" CPU: Unsupported number of siblings 4");
			logger.info("CPU Intel " + event.getNodeConfiguration().getCpuConfiguration().getCpuSpeed() +" (GHz) core i7 started...");
		}
	};

	/**
	 * This handler is triggered when a RESTART signal is received from OS
	 */
	Handler<RestartSignal> restartSignalHandler = new Handler<RestartSignal>() {
		@Override
		public void handle(RestartSignal event) {
			if (enabled) {
				enabled  = false;
				xySeries.clear();
				pt.clear();
				loadSamples.clear();
				loads.clear();
				tasks.set(0);
				gui.createCPULoadDiagram(getChart());
				scheduleRestart();
				logger.warn("CPU shutting down...");
			}
		}
	};
	
	/**
	 * Restarts the CPU
	 */
	Handler<Restart> restartHandler = new Handler<Restart>() {
		@Override
		public void handle(Restart event) {
			enabled = true;
			sendReadySignal();
			scheduleLoadSampler(1000);
			scheduleLoadCalculation();
			gui.createCPULoadDiagram(getChart());
			logger.warn("CPU Restarted...");
		}
	};
	
	/**
	 * This handler is triggered when a new process starts in the OS
	 */
	Handler<StartProcess> startProcessHandler = new Handler<StartProcess>() {
		@Override
		public void handle(StartProcess event) {
			if (enabled) {
				addToProcessTable(event.getProcess());
				tasks.incrementAndGet();
			}
		}
	};

	/**
	 * This handler is triggered when a process ends in the OS
	 */
	Handler<EndProcess> endProcessHandler = new Handler<EndProcess>() {
		@Override
		public void handle(EndProcess event) {
			if (enabled) {
				removeFromProcessTable(event.getPid());
				tasks.decrementAndGet();
			}
		}
	};
	
	/**
	 * Stores the current load of CPU
	 */
	Handler<LoadSamplerTimeout> loadSamplerTimeoutHandler = new Handler<LoadSamplerTimeout>() {
		@Override
		public void handle(LoadSamplerTimeout event) {
			if (enabled) {
				loadSamples.add(pt.size());
				scheduleLoadSampler(SAMPLER_INTERVAL);
			}
		}
	};
	
	/**
	 * This handler periodically compute load average
	 */
	Handler<LoadCalculationTimeout> loadCalculationTimeoutHandler = new Handler<LoadCalculationTimeout>() {
		@Override
		public void handle(LoadCalculationTimeout event) {
			if (enabled) {
				synchronized(loadSamples) {
					double load = 0.0;
					for (Integer sample : loadSamples) {
						load += sample;
					}
					load /= loadSamples.size();
					loadSamples.clear();
					loads.add(load);
					gui.cpuLoad(load);
					trigger(new CPULoad(load), cpu);
					xySeries.add(System.currentTimeMillis() - startTime, load);
					gui.createCPULoadDiagram(getChart());
				}
				scheduleLoadCalculation();
			}
		}
	};
	
	/**
	 * This handler is triggered when a new operation is started on the CPU
	 */
	Handler<AbstractOperation> abstractOperationHandler = new Handler<AbstractOperation>() {
		@Override
		public void handle(AbstractOperation event) {
			if (enabled) {
				tasks.incrementAndGet();
				for(int i=0; i<event.getNumberOfOperations(); i++)
					scheduleTimerForOperation(event.getDuration(CPU_CLOCK));
			}
		}
	};
	
	/**
	 * This handler is responsible for updating the current operation queue and removes the corresponding operation 
	 */
	Handler<OperationFinishedTimeout> operationFinishedTimeoutHandler = new Handler<OperationFinishedTimeout>() {
		@Override
		public void handle(OperationFinishedTimeout event) {
			if (enabled) {
	 			pt.remove(event.getPid());
				tasks.decrementAndGet();
			}
		}
	};
	
	/**
	 * This handler is triggered when a snapshot is issued by the user from OS
	 */
	Handler<SnapshotRequest> snapshotRequestHandler = new Handler<SnapshotRequest>() {
		@Override
		public void handle(SnapshotRequest event) {
			if (enabled) {
				event.setChart(getChart());
				trigger(event, cpu);
			}
		}
	};
	
	protected void sendReadySignal() {
		Ready ready = new Ready(Device.CPU);
		trigger(ready, cpu);
	}

	protected void scheduleRestart() {
		ScheduleTimeout st = new ScheduleTimeout(OS.RESTART_PERIOD);
		st.setTimeoutEvent(new Restart(st));
		trigger(st, timer);		
	}

	protected void scheduleTimerForOperation(long DURATION) {
		Process p = Process.createAbstractProcess();
		pt.put(p.getPid(), p);
		ScheduleTimeout st = new ScheduleTimeout(DURATION);
		OperationFinishedTimeout op = new OperationFinishedTimeout(st);
		op.setPid(p.getPid());
		st.setTimeoutEvent(op);
		trigger(st, timer);
	}

	protected void scheduleLoadCalculation() {
		ScheduleTimeout st = new ScheduleTimeout(LOAD_CALC_INTERVAL);
		st.setTimeoutEvent(new LoadCalculationTimeout(st));
		trigger(st, timer);		
	}
	
	protected void scheduleLoadSampler(long interval) {
		ScheduleTimeout st = new ScheduleTimeout(interval);
		st.setTimeoutEvent(new LoadSamplerTimeout(st));
		trigger(st, timer);
	}

	protected void addToProcessTable(Process process) {
		pt.put(process.getPid(), process);
	}

	protected void removeFromProcessTable(String pid) {
		pt.remove(pid);		
	}
	
	private JFreeChart getChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("CPU Load", "Time (ms)", "Cpu Load", dataSet, PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}
	
}
