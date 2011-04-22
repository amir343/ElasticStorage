package instance.os;

import org.jfree.chart.JFreeChart;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class InstanceSnapshot extends Snapshot {

	private JFreeChart cpuChart;
	private JFreeChart bandwidthChart;
	private String log;
	
	public InstanceSnapshot(int id) {
		super(id);
	}
	
	public void addCPULoadChart(JFreeChart chart) {
		this.cpuChart = chart;
	}
	
	public void addBandwidthChart(JFreeChart chart) {
		this.bandwidthChart = chart;
	}
	
	public void addLogText(String log) {
		this.log = log;
	}

	public JFreeChart getCpuChart() {
		return cpuChart;
	}

	public JFreeChart getBandwidthChart() {
		return bandwidthChart;
	}

	public String getLog() {
		return log;
	}
	
}
