package statistics.distribution;

import org.jfree.chart.JFreeChart;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public interface Distribution {

	long getNextValue();

	JFreeChart getChart();

	void setParameters(String... params);

}
