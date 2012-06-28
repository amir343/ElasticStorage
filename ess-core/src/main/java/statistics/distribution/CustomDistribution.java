/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package statistics.distribution;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-07
 *
 */

public class CustomDistribution implements Distribution {

	private List<String> lines;
	private int currentCounter;
	private int previousValue;

	public CustomDistribution(List<String> lines) {
		this.lines = lines;
		this.currentCounter = 0;
		this.previousValue = 5000;
	}

	@Override
	public long getNextValue() {
		if (currentCounter >= lines.size()-1 ) {
            currentCounter = 0;
			return previousValue;
		} else {
			previousValue = Integer.parseInt(lines.get(currentCounter).trim())*1000;
			if (previousValue <= 0) previousValue = 500;
			currentCounter++;
			return previousValue;
		}
	}

	@Override
	public JFreeChart getChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Workload", "Time (s)", "Timeout (s)", getDataset(), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}
	
	private XYDataset getDataset() {
		XYSeriesCollection dataSet = new XYSeriesCollection();
		XYSeries xySeries = new XYSeries("PDF");
		double x = 1;
		for (int i=0; i<lines.size(); i++) {
			int y = Integer.parseInt(lines.get(i).trim());
			x += y;
			xySeries.add(x, y);
		}
		dataSet.addSeries(xySeries);
		return dataSet;
	}

	@Override
	public void setParameters(String... params) {
		// TODO Auto-generated method stub

	}

}
