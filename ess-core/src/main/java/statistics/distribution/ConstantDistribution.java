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

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-07
 *
 */

public class ConstantDistribution implements Distribution {

	
	private int constantValue;

	public ConstantDistribution() {
		
	}
	
	@Override
	public long getNextValue() {
		return constantValue * 1000;
	}

	@Override
	public JFreeChart getChart() {
		JFreeChart chart = ChartFactory.createXYLineChart("Constant Distribution", "", "", getDataset(), PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}
	
	private XYDataset getDataset() {
		XYSeriesCollection dataSet = new XYSeriesCollection();
		XYSeries xySeries = new XYSeries("CDF");
		double x = 0.0;
		for (int i=0; i<6; i++) {
			xySeries.add(5, x);
			x+= 0.2;
		}
		dataSet.addSeries(xySeries);
		return dataSet;
	}


	@Override
	public void setParameters(String... params) {
		if (params.length >= 1) {
			constantValue = Integer.valueOf(params[0]);
		}

	}

}
