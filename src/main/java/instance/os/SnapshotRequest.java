package instance.os;

import org.jfree.chart.JFreeChart;
import se.sics.kompics.Event;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class SnapshotRequest extends Event {

	private JFreeChart chart;

	public SnapshotRequest() {
		
	}
	
	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	public JFreeChart getCPULoadChart() {
		return chart;
	}
	
}
