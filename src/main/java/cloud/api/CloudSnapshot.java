package cloud.api;

import instance.os.Snapshot;
import org.jfree.chart.JFreeChart;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class CloudSnapshot extends Snapshot {

	private JFreeChart chart;
	private String logText;

	public CloudSnapshot(int id) {
		super(id);
	}
	
	public void setResponseTimeChart(JFreeChart chart) {
		this.chart = chart;
	}
	
	public void addLogText(String log) {
		this.logText = log;
	}

	public JFreeChart getChart() {
		return chart;
	}

	public String getLog() {
		return logText;
	}

}
